#!/bin/bash    

set -eou pipefail

NIASSA_DIR=$1

DB_NAME=$2
DB_USER=$3
DB_PASSWORD=$4
DB_HOST=$5
DB_PORT=$6
MDB_NAME="metadb-$(date +%s)"

#start the metadb server
docker pull postgres:9-alpine
docker run --name ${MDB_NAME} --env POSTGRES_PASSWORD=${DB_PASSWORD} --env POSTGRES_DB=${DB_NAME} --env POSTGRES_USER=${DB_USER} -p ${DB_PORT}:5432 -d postgres:9-alpine

# not relying on local version of psql
export docker_postgres="docker run -it --rm -e PGPASSWORD=${DB_PASSWORD} --mount type=bind,source=${NIASSA_DIR},target=/niassa --link ${MDB_NAME}:${MDB_NAME} postgres:9-alpine"

# wait for the postgres docker to be running
while ! ${docker_postgres} pg_isready -h ${MDB_NAME} -U ${DB_USER} -d ${DB_NAME}; do
  sleep 1
done

#test data needs to be pre-loaded because otherwise tests fail
${docker_postgres} psql -h "${MDB_NAME}" -U "${DB_USER}" -d "${DB_NAME}" -w -f /niassa/seqware-meta-db/seqware_meta_db.sql
${docker_postgres} psql -h "${MDB_NAME}" -U "${DB_USER}" -d "${DB_NAME}" -w -f /niassa/seqware-meta-db/seqware_meta_db_data.sql 
${docker_postgres} psql -h "${MDB_NAME}" -U "${DB_USER}" -d "${DB_NAME}" -w -f /niassa/seqware-meta-db/seqware_meta_db_testdata.sql