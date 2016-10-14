#!/usr/bin/env python

import cipres
import os
import shutil
import subprocess
import sys

from os import path


def get_usage_message(script_name):
    return "usage: %s topdir confdir propfile target" % (path.basename(script_name))

def main(argv):
    try:
        if len(argv) != 5:
            raise cipres.BuildError(get_usage_message(argv[0]))

        top_dir = argv[1]
        conf_dir = argv[2]
        prop_file = argv[3]
        clean_target = False
        dependencies_target = False
        prepare_target = False
        package_target = False
        deploy_target = False

        if argv[4] == "clean":
            clean_target = True
        elif argv[4] == "dependencies":
            dependencies_target = True
        elif argv[4] == "prepare":
            prepare_target = True
        elif argv[4] == "package":
            package_target = True
        elif argv[4] == "deploy":
            deploy_target = True
        else:
            raise cipres.BuildError("unknown argument %s\n%s" % (argv[4], get_usage_message(argv[0])))

        if clean_target:
            return 0

        if dependencies_target:
            print "rest"

            return 0

        module_dir = path.dirname(argv[0])
        build_props = cipres.Properties(prop_file)

        if prepare_target:
            pass;

        if package_target:
            pass;

        if deploy_target:
            print "\ndeploying the REST client examples ..."
            java_umbrella_dir = path.join(module_dir, "examples", "java_umbrella")
            python_cipres_dir = path.join(module_dir, "examples", "python_cipres")

            os.chdir(python_cipres_dir)
            cipres.exec_command(["python", "setup.py", "install"])
            os.chdir(module_dir)

            os.chdir(java_umbrella_dir)
            cipres.exec_command(["mvn", "clean", "install"])

            """
                no longer deploying the java_umbrella example

            tomcat_dir = build_props.get("build.portal.tomcatDir", os.environ.get("CATALINA_HOME"))
            if tomcat_dir is None or len(tomcat_dir) == 0:
                raise cipres.BuildError("property build.portal.tomcatDir is missing or empty")
            tomcat_webapps_dir = path.join(tomcat_dir, "webapps")

            shutil.rmtree(path.join(tomcat_webapps_dir, "java_umbrella"), ignore_errors=True)
            shutil.copy2(path.join(java_umbrella_dir, "target", "java_umbrella.war"), tomcat_webapps_dir)
            """

        return 0

    except (IOError, OSError, cipres.BuildError) as err:
        sys.stderr.write("%s\n" % str(err))

        return 1

if __name__ == "__main__":
    sys.exit(main(sys.argv))
