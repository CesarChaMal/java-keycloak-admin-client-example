package de.tdlabs.training.keycloak

import java.util.Arrays.asList

import javax.ws.rs.core.Response

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation

object KeycloakAdminClientExampleKotlin {

    @Throws(Exception::class)
    fun main(args: Array<String>) {

        val kc = KeycloakBuilder.builder() //
                .serverUrl("http://localhost:8081/auth") //
                .realm("rest-example")//
                .username("rest-user-admin") //
                .password("password") //
                .clientId("admin-cli") //
                .resteasyClient(ResteasyClientBuilder().connectionPoolSize(10).build()) //
                .build()

        val credential = CredentialRepresentation()
        credential.setType(CredentialRepresentation.PASSWORD)
        credential.setValue("test123")
        credential.setTemporary(false)

        val user = UserRepresentation()
        user.setUsername("testuser")
        user.setFirstName("Test")
        user.setLastName("User")
        user.setCredentials(asList(credential))
        user.setEnabled(true)
        user.setRealmRoles(asList("admin"))

        // Create testuser
        val result = kc.realm("rest-example").users().create(user)
        if (result.getStatus() !== 201) {
            System.err.println("Couldn't create user.")
            System.exit(0)
        }
        System.out.println("Testuser created.... verify in keycloak!")

        System.out.println("Press any key...")
        System.`in`.read()

        // Delete testuser
        val locationHeader = result.getHeaderString("Location")
        val userId = locationHeader.replace(".*/(.*)$", "$1")
        kc.realm("rest-example").users().get(userId).remove()
    }

}
