package de.tdlabs.training.keycloak

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.util.function.Predicate

object KeycloakAdminClientExampleKotlin {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
//		String realm = "master";
        val realm = "jhipster"
        val user = "admin"
        //		String user = "user";
        val password = "admin"
        //		String clientId = "admin-cli";
//		String clientId = "jakarta-school";
        val clientId = "internal"
        //		String clientSecret = "Z1sgpbPnAuAeZWdBCQJABT9ASGkJoN1q";
//		String clientSecret = "U8QA7NVDNJNK5vR2Z8RqRZFzi6y4E5Ki";
        val clientSecret = "internal"
        val kc = KeycloakBuilder.builder()
                .serverUrl("http://localhost:9080/auth")
                .realm(realm)
                .username(user)
                .password(password)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(ResteasyClientBuilder().connectionPoolSize(10).build())
                .build()
        println(kc.tokenManager().accessToken.token)
        val credential = CredentialRepresentation()
        credential.type = CredentialRepresentation.PASSWORD
        credential.value = "test123"
        credential.isTemporary = false
        val newUser = UserRepresentation()
        newUser.username = "testuser"
        newUser.firstName = "Test"
        newUser.lastName = "User"
        newUser.credentials = listOf(credential)
        newUser.isEnabled = true
        newUser.realmRoles = listOf("admin")
        val realmResource = kc.realm(realm)
        val userResource = realmResource.users()

//		UserRepresentation userRepresentation = kc.realm(realm).users().get("admin").toRepresentation();
        val userRepresentation: UserRepresentation = userResource.list().first { representation: UserRepresentation? ->  representation!!.username == "user" }
//        val userRepresentation: UserRepresentation = userResource.list(). .filter(Predicate { u: UserRepresentation -> u.username == "user" }).findFirst().orElse(null)
        userRepresentation.isEnabled = false
        //		userRepresentation.setEnabled(true);
        println(userRepresentation.isEnabled)
        userResource[userRepresentation.id].update(userRepresentation)

        // Create testuser
        val result = kc.realm(realm).users().create(newUser)
        if (result.status != 201) {
            System.err.println("Couldn't create user.")
            System.exit(0)
        }
        println("Testuser created.... verify in keycloak!")
        println("Press any key...")
        System.`in`.read()

        // Delete testuser
        val locationHeader = result.getHeaderString("Location")
        val userId = locationHeader.replace(".*/(.*)$".toRegex(), "$1")
        kc.realm(realm).users()[userId].remove()
    }
}