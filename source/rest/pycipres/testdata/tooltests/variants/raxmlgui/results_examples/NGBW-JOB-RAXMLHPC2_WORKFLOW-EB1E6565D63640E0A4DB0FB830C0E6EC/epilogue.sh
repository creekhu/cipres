#!/bin/bash
    date +'%s %a %b %e %R:%S %Z %Y' > /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-EB1E6565D63640E0A4DB0FB830C0E6EC/term.txt
    echo "ExitCode=${10}" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-EB1E6565D63640E0A4DB0FB830C0E6EC/term.txt
    echo -e "Job Id: $1\nResource List: $6\nResources Used: $7\nQueue Name: $8\n" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-EB1E6565D63640E0A4DB0FB830C0E6EC/term.txt