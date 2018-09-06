#!/bin/sh
WRA=$(seqware --plugin io.seqware.pipeline.plugins.WorkflowScheduler -- --workflow-accession 4767 --ini-files $(dirname $0)/wr-872.ini --input-files 866,867,5190,870,871  --link-workflow-run-to-parents 4789  --host null | grep 'Created workflow run with SWID:' | cut -f 2 -d:)
echo Workflow run SWID: $WRA
