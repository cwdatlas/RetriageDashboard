/**
 * build.gradle.kts
 * @Author: Aidan Scott
 * @Author: John Botonakis
 */
plugins {
	// Apply the Java plugin to add support for Java language
	java
	// Apply the Spring Boot Gradle plugin for Spring Boot support
	id("org.springframework.boot") version "3.4.2"
	// Apply the Spring Dependency Management plugin to manage versions of dependencies
	id("io.spring.dependency-management") version "1.1.7"
}

// Define the project group ID
group = "com.retriage"
// Define the project version
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		// Configure the Java toolchain to use Java 21
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	// Use Maven Central repository for dependencies
	mavenCentral()
	//jcenter() repository is added for broader search, as a fallback for dependencies
	jcenter()
	//maven { url = uri("https://repo.maven.apache.org/maven2/") } // Explicit Maven Central URL -  (commented out as mavenCentral() already includes it)
	// Add Shibboleth Maven repository for potential OpenSAML or related artifacts if needed
	maven { url = uri("https://build.shibboleth.net/nexus/content/repositories/releases/") } // Add Shibboleth repo - for OpenSAML
}

dependencies {
	// Spring Boot Starter Web for building web applications
	implementation("org.springframework.boot:spring-boot-starter-web")
	// Spring Boot Starter Data JPA for database interaction using JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// Lombok for reducing boilerplate code in Java classes
	implementation("org.projectlombok:lombok")
	// Spring Boot Starter Thymeleaf for Thymeleaf templating engine
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	// Spring Boot Starter Test for testing Spring Boot applications
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	// MySQL Connector/J for connecting to MySQL databases (runtime dependency)
	runtimeOnly("com.mysql:mysql-connector-j")
	// JUnit Platform Launcher for running JUnit Platform tests (runtime dependency for tests)
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Okta Dependencies below:
	// Spring Boot Starter Security for Spring Security functionalities
	implementation("org.springframework.boot:spring-boot-starter-security");
	// Spring Boot Starter Web (again - likely redundant as already declared above, can be removed if no issues)
	implementation("org.springframework.boot:spring-boot-starter-web"); //likely redundant
	// Thymeleaf Extras Spring Security 6 integration for Spring Security with Thymeleaf
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6");
	// Spring Boot Starter Test (again - likely redundant as already declared above, can be removed if no issues)
	implementation("org.springframework.boot:spring-boot-starter-test"); //likely redundant
	// Spring Security Test for testing Spring Security functionalities
	implementation("org.springframework.security:spring-security-test");

	// Dependency constraints to enforce specific versions of OpenSAML libraries
	constraints {
		implementation ("org.opensaml:opensaml-core:4.1.1")
		implementation ("org.opensaml:opensaml-saml-api:4.1.1")
		implementation ("org.opensaml:opensaml-saml-impl:4.1.1")
	}
	// Spring Security SAML 2.0 Service Provider dependency
	implementation ("org.springframework.security:spring-security-saml2-service-provider")
}

tasks.withType<Test> {
	// Configure test tasks to use JUnit Platform
	useJUnitPlatform()
}