package com.example.server.service;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Service layer test suite")
@SelectPackages({
        "com.example.server.service",
        "com.example.server.service.mapper"
})
class ServiceTestSuite {
}
