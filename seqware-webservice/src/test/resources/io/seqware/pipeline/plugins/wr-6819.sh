#!/bin/sh
WRA=$(seqware --plugin io.seqware.pipeline.plugins.WorkflowScheduler -- --workflow-accession 6595 --ini-files $(dirname $0)/wr-6819.ini --link-workflow-run-to-parents 6820  --host null | grep 'Created workflow run with SWID:' | cut -f 2 -d:)
echo Workflow run SWID: $WRA
