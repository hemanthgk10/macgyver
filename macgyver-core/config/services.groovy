

println "services.groovy execution"
foo="bar"
services {

    foo {
        xyz {
            password="eyJrIjoibWFjMCIsImQiOiIrdTlEQVlPZVlld3Z4YXZ4VVdtU256OFM4UWxIUWFIOUVNc1REY0xxejM4PSJ9"
            password="eyJrIjoibWFjMCIsImQiOiIxdVREM1M2L3hmME1lY1IvVlFxaE83NmFMZjFCR1ZvZ2pHaHZWUGxiMVBzPSJ9"
        }
    }
   

}

myTestBean.foo="bar"


unittest.testBeanName.serviceType="testService"
unittest.testBeanName.foo="bar"

a.b.c.url="http://whatever"