

environments {
    junit {
        testds {
            serviceType="dataSource"
            jdbcUrl="jdbc:h2:mem:testdb"
            driverClassName="org.h2.Driver"
            username="sa"
            password=""
        }
    }
}