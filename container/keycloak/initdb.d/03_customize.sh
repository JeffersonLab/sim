#!/bin/bash

. /kc-lib.sh

echo "----------------"
echo "| Create Roles |"
echo "----------------"
KC_ROLE_NAME=acg
create_role

echo "----------------"
echo "| Assign Roles |"
echo "----------------"
KC_USERNAME=jsmith
assign_role

echo "------------------------------------"
echo "| Allow Password Grant (Test Only) |"
echo "------------------------------------"
# Integration tests get tokens for test users such as jsmith with their passwords
${KC_HOME}/bin/kcadm.sh update clients/${KC_CLIENT_NAME} -r "${KC_REALM}" -s directAccessGrantsEnabled=true
