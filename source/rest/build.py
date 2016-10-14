#!/usr/bin/env python

import cipres
import os
import shutil
import subprocess
import sys

from os import path


def get_usage_message(script_name):
    return "usage: %s topdir confdir propfile target" % (path.basename(script_name))


def check_admin_account(sdk_versions_dir):
    dev_null = open(os.devnull, "wb")

    try:
        args = [path.join(sdk_versions_dir, "cipresAdmin"), "-l"]
        process = subprocess.Popen(args, stdout=dev_null, stderr=sys.stdout)

        returncode = process.wait()

        if returncode != 0:
            print "There are no administrator accounts in the database.\nCreate one by running cipresAdmin. Then create the REST api\ntest and demo accounts by running testAndDemoAccounts."

    finally:
        dev_null.close()


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

        module_dir = path.dirname(argv[0])

        if clean_target:
            datatypes_dir = path.join(module_dir, "datatypes")
            schemas_dir = path.join(datatypes_dir, "schemas")
            target_dir = path.join(datatypes_dir, "target")

            if path.exists(schemas_dir):
                shutil.rmtree(schemas_dir)

            if path.exists(target_dir):
                shutil.rmtree(target_dir)

            return 0

        if dependencies_target:
            print "emailbean"
            print "sdk"
            print "tus_servlet"
            print "tus_cipres"

            return 0

        module_build_dir = path.join(top_dir, "build", "rest")
        conf_dir = path.join(conf_dir, "rest")
        build_props = cipres.Properties(prop_file)

        if prepare_target:
            print "\npreparing the REST service source code..."

            cipresrest_build_src_dir = path.join(module_build_dir, "cipresrest", "src")
            cipresrest_conf_src_dir = path.join(conf_dir, "cipresrest", "src")
            datatypes_build_src_dir = path.join(module_build_dir, "datatypes", "src")
            datatypes_conf_src_dir = path.join(conf_dir, "datatypes", "src")
            pycipres_build_src_dir = path.join(module_build_dir, "pycipres")
            pycipres_conf_src_dir = path.join(conf_dir, "pycipres")
            cipres_java_client_build_src_dir = path.join(module_build_dir, "cipres_java_client")
            cipres_java_client_conf_src_dir = path.join(conf_dir, "cipres_java_client")

            if not path.exists(module_build_dir):
                os.mkdir(module_build_dir, 0775)

            ## cipresrest
            if not path.exists(cipresrest_build_src_dir):
                os.makedirs(cipresrest_build_src_dir, 0775)
            cipres.copy_tree(build_props, path.join(module_dir, "cipresrest", "src"), cipresrest_build_src_dir)
            if path.exists(cipresrest_conf_src_dir):
                cipres.copy_tree(build_props, cipresrest_conf_src_dir, cipresrest_build_src_dir)

            ## datatypes
            if not path.exists(datatypes_build_src_dir):
                os.makedirs(datatypes_build_src_dir, 0775)
            cipres.copy_tree(None, path.join(module_dir, "datatypes", "src"), datatypes_build_src_dir)
            if path.exists(datatypes_conf_src_dir):
                cipres.copy_tree(None, datatypes_conf_src_dir, datatypes_build_src_dir)

            ## pycipres
            if not path.exists(pycipres_build_src_dir):
                os.mkdir(pycipres_build_src_dir, 0775)
            cipres.copy_tree(None, path.join(module_dir, "pycipres"), pycipres_build_src_dir)
            if path.exists(pycipres_conf_src_dir):
                cipres.copy_tree(None, pycipres_conf_src_dir, pycipres_build_src_dir)

            ## cipres_java_client
            if not path.exists(cipres_java_client_build_src_dir):
                os.makedirs(cipres_java_client_build_src_dir, 0775)
            cipres.copy_tree(None, path.join(module_dir, "cipres_java_client", "src"), cipres_java_client_build_src_dir)
            if path.exists(cipres_java_client_conf_src_dir):
                cipres.copy_tree(None, javaclient_conf_src_dir, cipres_java_client_build_src_dir)


        if package_target:
            print "\nbuilding the REST service...\n"

            python_install = build_props.get("python.install", "develop")

            os.chdir(module_dir)

            cipres.exec_command(["mvn", "-Dpython.install=%s" % (python_install), "install"])

        if deploy_target:
            print "\ndeploying the REST service..."

            sdk_versions_dir = build_props.get("build.sdk.versionsDir", os.environ.get("SDK_VERSIONS"))

            if sdk_versions_dir is None or len(sdk_versions_dir) == 0:
                raise cipres.BuildError("property build.sdk.versionsDir is missing or empty")

            dest_script_name = path.join(sdk_versions_dir, "testAndDemoAccounts")
            test_data_dir = path.join(sdk_versions_dir, "testdata")

            shutil.copy2(path.join(module_dir, "cipresrest", "testAndDemoAccounts"), dest_script_name)
            os.chmod(dest_script_name, 0775)

            shutil.rmtree(test_data_dir, ignore_errors=True)
            shutil.copytree(path.join(module_build_dir, "pycipres", "target", "testdata"), test_data_dir)
            os.chmod(path.join(test_data_dir, "pycipres.conf"), 0600)

            tomcat_dir = build_props.get("build.portal.tomcatDir", os.environ.get("CATALINA_HOME"))

            if tomcat_dir is None or len(tomcat_dir) == 0:
                raise cipres.BuildError("property build.portal.tomcatDir is missing or empty")

            tomcat_webapps_dir = path.join(tomcat_dir, "webapps")
            app_name = build_props.get("build.rest.appName")

            shutil.rmtree(path.join(tomcat_dir, "work"), ignore_errors=True)
            shutil.rmtree(path.join(tomcat_webapps_dir, app_name), ignore_errors=True)
            shutil.copy2(path.join(module_build_dir, "cipresrest", "target", "%s.war" % (app_name)), tomcat_webapps_dir)

            check_admin_account(sdk_versions_dir)

        return 0

    except (IOError, OSError, cipres.BuildError) as err:
        sys.stderr.write("%s\n" % str(err))

        return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))
