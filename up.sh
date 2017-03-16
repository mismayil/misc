#!/bin/bash
#
# Script to cd fast
# March 16, 2017, Mahammad Ismayilzada
#
# alias up=source up.sh
# usage: up [n=1]
# Go up by n directories

STEPS=1
RE="^[0-9]+$"

if [[ -n "$1" && "$1" =~ $RE ]]; then
    STEPS=$1
fi

DIR=

while [ $STEPS -ne 0 ]; do
    DIR=../$DIR
    STEPS=$((STEPS-1))
done

cd $DIR
