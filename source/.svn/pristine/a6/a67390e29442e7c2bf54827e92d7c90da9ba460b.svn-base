#!/usr/bin/env python

import cipres
import os
import shutil
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
            print "tus_servlet"
            print "tus_cipres"
            print "sdk" 

            return 0

        module_dir = path.dirname(argv[0])
        module_build_dir = path.join(top_dir, "build", "portal")
        build_props = cipres.Properties(prop_file)

        if prepare_target:
            print "\npreparing the portal source code..."

            module_build_src_dir = path.join(module_build_dir, "src")
            conf_module_src_dir = path.join(conf_dir, "portal", "src")

            if not path.exists(module_build_dir):
                os.mkdir(module_build_dir, 0775)

            cipres.copy_tree(None, path.join(module_dir, "src"), module_build_src_dir)

            if path.exists(conf_module_src_dir):
                cipres.copy_tree(None, conf_module_src_dir, module_build_src_dir)

        if package_target:
            print "\nbuilding the portal...\n"

            enable_iplant = build_props.get("portal.enableIplant", "0")

            os.chdir(module_dir)

            cipres.exec_command(["mvn", "-Diplant=%s" % (enable_iplant), "install"])

        if deploy_target:
            print "\ndeploying the portal..."

            tomcat_dir = build_props.get("build.portal.tomcatDir", os.environ.get("CATALINA_HOME"))

            if tomcat_dir is None or len(tomcat_dir) == 0:
                raise cipres.BuildError("property build.portal.tomcatDir is missing or empty")

            tomcat_webapps_dir = path.join(tomcat_dir, "webapps")
            app_name = build_props.get("build.portal.appName")

            shutil.rmtree(path.join(tomcat_dir, "work"), ignore_errors=True)
            shutil.rmtree(path.join(tomcat_webapps_dir, app_name), ignore_errors=True)
            shutil.copy2(path.join(module_build_dir, "target", "%s.war" % (app_name)), tomcat_webapps_dir)

        return 0

    except (IOError, OSError, cipres.BuildError) as err:
        sys.stderr.write("%s\n" % str(err))

        return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))
