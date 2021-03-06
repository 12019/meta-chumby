inherit chumbysg-git chumby-info

require recipes/linux/linux.inc

PR = "r9"

COMPATIBLE_MACHINE = "chumby-wintergrasp"

PROVIDES = "virtual/kernel"

SRC_URI = "${CHUMBYSG_GIT_HOST}/chumby-sg/linux-2.6.35.3-wintergrasp${CHUMBYSG_GIT_EXTENSION};subpath=src;protocol=${CHUMBYSG_GIT_PROTOCOL} \
           file://000-fix_sd_wp.patch \
           file://001-chumby_rev.patch \
           file://002-chumby_lcd.patch \
           file://003-chumby_codec_cx2074x.patch \
           file://004-chumby_ts_xpt2046.patch \
           file://005-chumby_keyboard.patch \
           file://006-freescale_usb_boot.patch \
           file://007-chumby_timer.patch \
           file://008-fix_cpufreq_hang_on_boot.patch \
           file://009-fix_uboot_wintergrasp_config.patch \
           file://010-memory_debug_tool.patch \
           file://011-fb_stable_during_boot.patch \
           file://012-allow_setup_mtd_parts_from_uboot.patch \
           file://013-hack_allow_host_as_device.patch \
           file://defconfig \
"
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/src"

# Mark archs/machines that this kernel supports
DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_chumby-wintergrasp = "1"
