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
