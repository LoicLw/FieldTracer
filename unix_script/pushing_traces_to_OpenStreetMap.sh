#!/bin/csh -fx
 
# This script can be used to get all the GPS traces (GPX files) in the
#mesh network that all humanitarian mappers produced
#
# It should be run in a regular cron job on an internet-connected servald
# instance that receives the bundles (containing the traces).
# 
# This file should be place in an configured "batphone/jni/serval_dna" # or a "serval-dna" directory
# See https://github.com/servalproject/serval-dna
#
# Don't forget to chmod +x pushing_traces_to_OpenStreetMap.sh
 
# Change extension to suit
set EXTENSION=.gpx
# Configure username and password
set USERNAME=TestAccount_Srvl
set PASSWORD=
 
# Change this to wherever the OSM provided upload URL is.
set URL=http://api.openstreetmap.org/api/0.6/gpx/create
 
foreach bid (`./servald rhizome list  | grep file | grep $EXTENSION | cut -f3 -d:`)
   if ( ! -e uploaded/$bid ) then
      set filename=`./servald rhizome list | grep ":${bid}:" | cut -f13 -d:`
      echo $bid $filename
      ./servald rhizome extract file $bid trace.temp
      if ( $status == 0 ) then
        ls -l trace.temp
        # push to OSM server
        curl -v -u USERNAME:PASSWORD -F "description=$filename" -F "tag=$bid" -F "visibility=private" -F "file=@trace.temp" $URL
        echo $status
        # if that succeeds:
        if ( $status == 0 ) then
          touch uploaded/$bid
        endif
      endif
      rm trace.temp
   endif
end