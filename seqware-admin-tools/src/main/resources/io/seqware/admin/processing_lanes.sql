select wr.workflow_run_id, pl.lane_id, l.sw_accession as lane_swid, p.processing_id
from workflow_run wr
left join processing p on (wr.workflow_run_id = p.workflow_run_id OR wr.workflow_run_id = p.ancestor_workflow_run_id)
left join processing_lanes pl on (p.processing_id = pl.processing_id)
left join lane l on (pl.lane_id = l.lane_id)
where pl.lane_id is not null