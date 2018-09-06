#!/bin/sh
WRA=$(seqware --plugin io.seqware.pipeline.plugins.WorkflowScheduler -- --workflow-accession 2861 --ini-files $(dirname $0)/wr-863.ini --input-files 838  --host null | grep 'Created workflow run with SWID:' | cut -f 2 -d:)
echo Workflow run SWID: $WRA
