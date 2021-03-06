DESCRIPTION = "Simple DirectMedia Layer (DirectFB and Framebuffer support)"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "LGPL"
INC_PR = "r1"
EXTRA_OECONF = "--program-transform-name=s/libSDL/libSDL-chumby-simple/g"

SRC_URI = "http://www.libsdl.org/release/SDL-${PV}.tar.gz \
           file://0001-chumby-netv-keyboard.patch \
           file://Makefile.chumby \
           file://SDL_config_chumby.h \
"
SRC_URI[md5sum] = "e52086d1b508fa0b76c52ee30b55bec4"
SRC_URI[sha256sum] = "5d927e287034cb6bb0ebccfa382cb1d185cb113c8ab5115a0759798642eed9b6"

S = "${WORKDIR}/SDL-${PV}"

PR = "${INC_PR}.8"

do_compile() {
    if [ -e ../Makefile.chumby ]; then mv ../Makefile.chumby .; fi
    if [ -e ../SDL_config_chumby.h ]; then mv ../SDL_config_chumby.h include/SDL_config.h; fi
    make -f Makefile.chumby
}

do_install() {
    install -d ${D}/usr/lib
    mv libSDL.a ${D}/usr/lib/libSDL-chumby-simple.a
}
    

EXTRA_OECONF = " \
  --disable-static --disable-debug --disable-cdrom --disable-threads --enable-timers --enable-endian \
  --enable-file --disable-oss --disable-alsa --disable-esd --disable-arts \
  --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
  --disable-mintaudio --disable-nasm --disable-video-x11 --disable-video-dga \
  --enable-video-fbcon --disable-video-directfb --disable-video-ps2gs \
  --disable-video-xbios --disable-video-gem --disable-video-dummy \
  --disable-video-opengl --enable-input-events --disable-pthreads \
  --disable-video-picogui --disable-video-qtopia --enable-dlopen \
  --disable-input-tslib --disable-video-ps3 --disable-rpath \
"
