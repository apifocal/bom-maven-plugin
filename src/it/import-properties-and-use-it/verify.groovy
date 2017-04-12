File propertiesFile = new File( basedir, "target/classes/test.properties" )

assert propertiesFile.isFile()

Properties properties = new Properties()
propertiesFile.withInputStream { 
    properties.load(it)
}

// check some imports
assert properties."commons-pool.version" == "1.6"
assert properties."commons-io.version" == "2.4"

