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
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	runtimeOnly("com.mysql:mysql-connector-j")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	//Okta Dependencies below:
	implementation("org.springframework.boot:spring-boot-starter-security");
//	implementation("org.springframework.security:spring-security-saml2-service-provider") // SAML2

}

tasks.withType<Test> {
	useJUnitPlatform()
}

//Needed for forcing dependency in Okta
configurations.all {
	resolutionStrategy.force("org.opensaml:opensaml-saml-impl:4.0.1")// Or try 4.1.1 if 4.0.1 doesn't work later
}