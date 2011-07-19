DESCRIPTION = "Utilities for driving NeTV"
LICENSE = "BSD"

inherit update-rc.d

PR = "r24"

SRC_URI = "file://helpers/dumpreg.c \
	file://helpers/putreg.c \
	file://helpers/parse-edid.c \
	file://helpers/snoop.c \
	file://helpers/setbox.c \
	file://helpers/modeline.c \
	file://helpers/compute_ksv.c \
	file://helpers/derive_km.c \
	file://helpers/writecached_Km.c \
	file://helpers/fpga_ctl.c \
	file://helpers/chumby_xilinx.h \
	file://fpga/hdmi_overlay.bin \
	file://fpga/hdmi_720p.bin \
	file://fpga/min720p.edid \
	file://fpga/min1080p24.edid \
	file://fpga/41-chumby-netv.rules \
	file://helpers/dumptiming.c \
	file://helpers/netv_service \
	file://helpers/matchmode.c \
	file://gnupg/pubring.gpg \
	file://gnupg/secring.gpg \
	file://gnupg/trustdb.gpg \
"

S = "${WORKDIR}"

do_compile() {
    cd ${S}
    ${CC} ${CFLAGS} ${LDFLAGS} -o dumpreg helpers/dumpreg.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o dumptiming helpers/dumptiming.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o putreg helpers/putreg.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o parse-edid.o -c helpers/parse-edid.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o snoop helpers/snoop.c parse-edid.o
    ${CC} ${CFLAGS} ${LDFLAGS} -o setbox helpers/setbox.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o modeline helpers/modeline.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o matchmode helpers/matchmode.c

    ${CC} ${CFLAGS} ${LDFLAGS} -c helpers/compute_ksv.c
    ${CC} ${CFLAGS} ${LDFLAGS} -o derive_km helpers/derive_km.c compute_ksv.o
    ${CC} ${CFLAGS} ${LDFLAGS} -o writecached_Km helpers/writecached_Km.c

    ${CC} ${CFLAGS} ${LDFLAGS} -o fpga_ctl helpers/fpga_ctl.c
}

do_install() {
        install -d ${D}${sysconfdir}/init.d
	install -m 0755 helpers/netv_service ${D}${sysconfdir}/init.d/netv_service

	install -d ${D}${bindir}
	install -m 0755 dumpreg ${D}${bindir}
	install -m 0755 dumptiming ${D}${bindir}
	install -m 0755 putreg ${D}${bindir}
	install -m 0755 snoop ${D}${bindir}
	install -m 0755 setbox ${D}${bindir}
	install -m 0755 modeline ${D}${bindir}
	install -m 0755 derive_km ${D}${bindir}
	install -m 0755 writecached_Km ${D}${bindir}
	install -m 0755 fpga_ctl ${D}${bindir}
	install -m 0755 matchmode ${D}${bindir}

	install -d ${D}/${base_libdir}/firmware
	install -m 0644 fpga/hdmi_overlay.bin ${D}${base_libdir}/firmware/
	install -m 0644 fpga/hdmi_720p.bin ${D}${base_libdir}/firmware/
	install -m 0644 fpga/min720p.edid ${D}${base_libdir}/firmware/
	install -m 0644 fpga/min1080p24.edid ${D}${base_libdir}/firmware/

	install -d ${D}${base_libdir}/udev/rules.d
	install -m 0644 fpga/41-chumby-netv.rules ${D}${base_libdir}/udev/rules.d

	install -d ${D}/home/root/.gnupg
	install -m 0600 gnupg/pubring.gpg ${D}/home/root/.gnupg/pubring.gpg
	install -m 0600 gnupg/secring.gpg ${D}/home/root/.gnupg/secring.gpg
	install -m 0600 gnupg/trustdb.gpg ${D}/home/root/.gnupg/trustdb.gpg
}

FILES_${PN} = "${bindir}"
FILES_${PN} += "${base_libdir}/firmware/"
FILES_${PN} += "${base_libdir}/udev/rules.d/"
FILES_${PN} += "${sysconfdir}/init.d/"
FILES_${PN} += "/home/root/.gnupg/pubring.gpg"
FILES_${PN} += "/home/root/.gnupg/secring.gpg"
FILES_${PN} += "/home/root/.gnupg/trustdb.gpg"
PACKAGE_ARCH = "${MACHINE}"

INITSCRIPT_NAME = "netv_service"
INITSCRIPT_PARAMS = "defaults 50 50"
