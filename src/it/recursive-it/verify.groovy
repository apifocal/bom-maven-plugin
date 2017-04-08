File propertiesFile = new File( basedir, "target/classes/test.properties" )

assert propertiesFile.isFile()

Properties properties = new Properties()
propertiesFile.withInputStream { 
    properties.load(it)
}

// direct imports
assert properties."maven.compiler.source" == "1.6"
assert properties."maven.compiler.target" == "1.6"
assert properties."commons.componentid" == "io"
assert properties."commons.rc.version" == "RC4"
assert properties."commons.release.version" == "2.5"
assert properties."commons.release.desc" == "(requires JDK 1.6+)"
assert properties."commons.jira.id" == "IO"
assert properties."commons.jira.pid" == "12310477"
assert properties."commons.scmPubCheckoutDirectory" == "site-content"


// nothing imported from parent with recursive == false
//assert properties."commons.release.version" != "39"
assert properties."commons.surefire.version" == "2.18.1"
assert properties."commons.surefire-report.version" == "2.18.1"
assert properties."commons.javadoc.version" == "2.10.3"
assert properties."commons.rat.version" == "0.11"
assert properties."commons.changes.version" == "2.11"
assert properties."commons.clirr.version" == "2.6.1"
assert properties."commons.jxr.version" == "2.5"
assert properties."commons.project-info.version" == "2.8"

