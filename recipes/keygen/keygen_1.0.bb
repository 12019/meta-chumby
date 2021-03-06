inherit chumbysg-git

DESCRIPTION = "Cryptoprocessor daemon, used in lieu of a real processor"
LICENSE = "BSD"

PR = "r7"

SRC_URI = "${CHUMBYSG_GIT_HOST}/${PN}.git;protocol=${CHUMBYSG_GIT_PROTOCOL}"
SRCREV = "c591f5c5efbf75aaa1eebb687f8830f0ddad2b14"

S = "${WORKDIR}/git/src/keygen"
DEPENDS = "beecrypt"
RDEPENDS_${PN} = "beecrypt config-util"

PACKAGE_ARCH = "${MACHINE}"

CHUMBY_CFLAGS = ""
CHUMBY_CFLAGS_chumby-silvermoon-netv = "-DUSE_FPGA_ENTROPY"
CHUMBY_CFLAGS_chumby-falconwing = "-DUSE_ACCEL_ENTROPY"

do_compile() {
    ${CC} keygen.c -o keygen ${CFLAGS} ${CHUMBY_CFLAGS} ${LDFLAGS} -lbeecrypt
}

do_install() {
    install -d ${D}/usr/bin
    install -m 0755 keygen ${D}/usr/bin
}

pkg_postinst_${PN}() {
    if test "x$D" != "x"; then exit 1; fi  # Don't do postinst on build system

    KEYS=$(config_util --cmd=getblock --block=cpid | hexdump -C | grep '00 01 00 01' | wc -l)
    if [ -z ${KEYS} ]
    then
        KEYS=0
    fi
    if [ ${KEYS} -ge 15 ]
    then
        echo "Not regenerating keys.  To force a regeneration, run the following:"
        echo "config_util --cmd=putblock --block=cpid < /dev/zero; opkg install keygen"
    else
        ifconfig wlan0 up
        ifconfig wlan1 up
        keygen `fpga_ctl n | cut -d" " -f3` 00000000000000000000000000060000
        config_util --cmd=putblock --block=cpid < /tmp/keyfile
        /etc/init.d/chumby-cpid restart
        rm -f /tmp/keyfile*
    fi
    (while [ -e /usr/lib/opkg/lock ]; do sleep 1; done; opkg remove keygen)&
}
