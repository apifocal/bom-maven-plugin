File propsFile1 = new File( basedir, "bom-client-1/target/classes/test.properties" )
File propsFile2 = new File( basedir, "bom-client-2/target/classes/test.properties" )

assert propsFile1.isFile()
assert propsFile2.isFile()

Properties props1 = new Properties()
Properties props2 = new Properties()

propsFile1.withInputStream { props1.load(it) }
propsFile2.withInputStream { props2.load(it) }

assert props1."test.property" == "test.value"
assert props1."test.property.1" == "test.value.1"
assert props1."test.property.2" == "\${test.property.2}"
assert props1."commons.release.version" == "\${commons.release.version}"

assert props2."test.property" == "test.value"
assert props2."test.property.1" == "test.value.1"
assert props2."test.property.2" == "test.value.2"
assert props2."commons.release.version" == "\${commons.release.version}"