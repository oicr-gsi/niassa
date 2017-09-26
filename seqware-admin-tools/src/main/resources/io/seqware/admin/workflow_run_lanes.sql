select wr.workflow_run_id, lwr.lane_id, l.sw_accession as lane_swid
from workflow_run wr
right join lane_workflow_runs lwr on wr.workflow_run_id = lwr.workflow_run_id
left join lane l on lwr.lane_id = l.lane_id