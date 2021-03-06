#!/bin/bash

# RANDOM is a random number between 0 and 32768.  Happily, 32768 very
# nearly divides evenly into 3600 (leaving 1/450 over).  This is used
# to throttle connections to the server.

# change to 900 seconds 8/30/2011 -- temporary for beta, revert to 3600 for release
DELAY=$(($RANDOM%900))
POSSIBLE_URLS="http://netv.bunnie-bar.com/updates/chumby-silvermoon-netv/update.sh http://netv.sutajiokousagi.com/updates/chumby-silvermoon-netv/update.sh http://buildbot.chumby.com.sg/updates/chumby-silvermoon-netv/update.sh"
URL=
SCRIPT_PATH=/tmp/update.$$.sh
LAST_ETAG_PATH=/tmp/update.last-etag
UPDATE_DEBUG=0
if [ -e /test-updates ]
then
	UPDATE_DEBUG=1
fi

# Ensure only one copy is running at a time
if [ $(ps ax | grep "$0" | grep -v grep | wc -l) -gt 2 ]
then
	logger -t update "$0 is already running - exiting"
	exit 0
fi


# To ensure all the update scripts don't hammer the server, pause for some amount of time.
if [ ${UPDATE_DEBUG} -eq 0 ]
then
	logger -t update "pausing for ${DELAY} seconds before checking update"
	sleep $DELAY
fi


# Loop through all possible URLs to try and find one that's active
for i in ${POSSIBLE_URLS}
do
    ETAG=$(curl --stderr /dev/null -I "$i" | grep -i ^etag: | cut -d'"' -f2)
    if [ ! -z ${ETAG} ]
    then
        URL=${i}
        break
    fi
done

if [ -z ${URL} ]
then
    logger -t update "unable to find active update host"
    exit 1
fi


# We use the ETag to determine if the file has changed or not.
if [ -f $LAST_ETAG_PATH ]
then
	if [ "x$(cat $LAST_ETAG_PATH)" = "x${ETAG}" ]
	then
		if [ ${UPDATE_DEBUG} -eq 0 ]
		then
			logger -t update "etag hasn't changed - ${ETAG}"
			exit 0
		fi
	fi
fi
echo "${ETAG}" > $LAST_ETAG_PATH


# Either this is the first time we've run, or the ETag is new.  Fetch the script.
if ! curl --stderr /dev/null -f -o $SCRIPT_PATH "$URL"
then
	logger -t update "unable to locate update file - $URL"
	rm -f ${$LAST_ETAG_PATH}
	exit 0
fi


# Download the script's key
if ! curl --stderr /dev/null -f -o $SCRIPT_PATH.sig "$URL.sig"
then
	logger -t update "unable to locate update signature file - $URL.sig"
	rm -f ${$LAST_ETAG_PATH}
	exit 0
fi


# Verify the script's signature matches, if we're on release.
if ! gpg --lock-never --no-options --no-default-keyring --keyring /etc/opkg/trusted.gpg --secret-keyring /etc/opkg/secring.gpg --trustdb-name /etc/opkg/trustdb.gpg --quiet --batch --verify $SCRIPT_PATH.sig
then
	logger -t update "update script does not match signature"
	if ! grep -q silvermoon-netv-debug /etc/opkg/*.conf
	then
		if [ ${UPDATE_DEBUG} -eq 0 ]
		then
			logger -t update "abandoning update process"
			rm -f ${$LAST_ETAG_PATH}
			exit 0
		fi
	fi
	logger -t update "on debug branch: continuing update anyway"
fi


# Run the script
chmod a+x $SCRIPT_PATH
exec $SCRIPT_PATH
