#!/bin/sh
set -eu

TEMPLATE="/pgpassfile.template"
TARGET="/var/lib/pgadmin/.pgpass"

cp "${TEMPLATE}" "${TARGET}"
chmod 600 "${TARGET}"

exec /entrypoint.sh "$@"
