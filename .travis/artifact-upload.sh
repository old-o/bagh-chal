#! /bin/bash

user_info="$BINTRAY_USER:$BINTRAY_API_KEY"
build_dir="$TRAVIS_BUILD_DIR/target"

file="$(ls ${build_dir}/*.jar | head -n 1)"
upload_url='https://api.bintray.com/content/odoepner/generic/bagh-chal/0.0.2/bagh-chal.jar?override=1&publish=1'

if [ -z "$file" ]; then
  echo "ERROR: Cannot upload. No jar file found in $build_dir"
  exit 1
else
  echo "Uploading $file to $upload_url"
  curl --user "${user_info}" --upload-file "${file}" "${upload_url}"
fi
