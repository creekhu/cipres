#!/bin/bash
    date +'%s %a %b %e %R:%S %Z %Y' > /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_XSEDE-A2AB5A07B7094403B19E213FF014DB73/term.txt
    echo "ExitCode=${10}" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_XSEDE-A2AB5A07B7094403B19E213FF014DB73/term.txt
    echo -e "Job Id: $1\nResource List: $6\nResources Used: $7\nQueue Name: $8\n" >> /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC8_XSEDE-A2AB5A07B7094403B19E213FF014DB73/term.txt