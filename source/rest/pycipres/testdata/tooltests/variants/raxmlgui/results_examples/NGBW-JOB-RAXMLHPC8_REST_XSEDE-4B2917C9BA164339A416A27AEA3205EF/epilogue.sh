#!/bin/bash
    date +'%s %a %b %e %R:%S %Z %Y' > /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_REST_XSEDE-4B2917C9BA164339A416A27AEA3205EF/term.txt
    echo "ExitCode=${10}" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_REST_XSEDE-4B2917C9BA164339A416A27AEA3205EF/term.txt
    echo -e "Job Id: $1\nResource List: $6\nResources Used: $7\nQueue Name: $8\n" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_REST_XSEDE-4B2917C9BA164339A416A27AEA3205EF/term.txt