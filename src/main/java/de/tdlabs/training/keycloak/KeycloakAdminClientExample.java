package de.tdlabs.training.keycloak;

import static java.util.Arrays.asList;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakAdminClientExample {

	public static void main(String[] args) throws Exception {

		Keycloak kc = KeycloakBuilder.builder() //
				.serverUrl("http://localhost:9080/auth") //
				.realm("jhipster")//
				.username("admin") //
				.password("admin") //
				.clientId("admin-cli") //
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
				.build();

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue("test123");
		credential.setTemporary(false);

		UserRepresentation user = new UserRepresentation();
		user.setUsername("testuser");
		user.setFirstName("Test");
		user.setLastName("User");
		user.setCredentials(asList(credential));
		user.setEnabled(true);
		user.setRealmRoles(asList("admin"));

		// Create testuser
		Response result = kc.realm("jhipster").users().create(user);
		if (result.getStatus() != 201) {
			System.err.println("Couldn't create user.");
			System.exit(0);
		}
		System.out.println("Testuser created.... verify in keycloak!");

		System.out.println("Press any key...");
		System.in.read();

		// Delete testuser
		String locationHeader = result.getHeaderString("Location");
		String userId = locationHeader.replaceAll(".*/(.*)$", "$1");
		kc.realm("jhipster").users().get(userId).remove();
	}

}
