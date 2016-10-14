#!/bin/bash
    date +'%s %a %b %e %R:%S %Z %Y' > /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-83E43D35128F4377817BBD9C8FC94932/term.txt
    echo "ExitCode=${10}" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-83E43D35128F4377817BBD9C8FC94932/term.txt
    echo -e "Job Id: $1\nResource List: $6\nResources Used: $7\nQueue Name: $8\n" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_WORKFLOW-83E43D35128F4377817BBD9C8FC94932/term.txt