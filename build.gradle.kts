plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.retriage"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
//	maven { url = uri("https://repo.maven.apache.org/maven2/") } // Keep explicit Maven Central URL
	jcenter() // ADD jcenter() repository - for broader search, as a fallback
	maven { url = uri("https://build.shibboleth.net/nexus/content/repositories/releases/") } // Add Shibboleth repo
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	runtimeOnly("com.mysql:mysql-connector-j")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	//Okta Dependencies below:
	implementation("org.springframework.boot:spring-boot-starter-security");
	implementation("org.springframework.boot:spring-boot-starter-web");
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6");
	implementation("org.springframework.boot:spring-boot-starter-test");
	implementation("org.springframework.security:spring-security-test");
//	implementation("org.springframework.security:spring-security-saml2-service-provider")
//	implementation("org.opensaml:opensaml-core:4.3.0")
//	implementation("org.opensaml:opensaml-saml-api:4.3.0")
//	implementation("org.opensaml:opensaml-saml-impl:4.3.0")

	constraints {
		implementation ("org.opensaml:opensaml-core:4.1.1")
		implementation ("org.opensaml:opensaml-saml-api:4.1.1")
		implementation ("org.opensaml:opensaml-saml-impl:4.1.1")
	}
	implementation ("org.springframework.security:spring-security-saml2-service-provider")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
