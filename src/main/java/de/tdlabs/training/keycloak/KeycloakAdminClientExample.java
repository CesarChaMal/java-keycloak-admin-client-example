package de.tdlabs.training.keycloak;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class KeycloakAdminClientExample {

	public static void main(String[] args) throws Exception {
//		String realm = "master";
		String realm = "jhipster";
		String user = "admin";
//		String user = "user";
		String password = "admin";
//		String clientId = "admin-cli";
//		String clientId = "jakarta-school";
		String clientId = "internal";
//		String clientSecret = "Z1sgpbPnAuAeZWdBCQJABT9ASGkJoN1q";
//		String clientSecret = "U8QA7NVDNJNK5vR2Z8RqRZFzi6y4E5Ki";
		String clientSecret = "internal";

		Keycloak kc = KeycloakBuilder.builder()
				.serverUrl("http://localhost:9080/auth")
				.realm(realm)
				.username(user)
				.password(password)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();
		System.out.println(kc.tokenManager().getAccessToken().getToken());

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue("test123");
		credential.setTemporary(false);

		UserRepresentation newUser = new UserRepresentation();
		newUser.setUsername("testuser");
		newUser.setFirstName("Test");
		newUser.setLastName("User");
		newUser.setCredentials(Collections.singletonList(credential));
		newUser.setEnabled(true);
		newUser.setRealmRoles(Collections.singletonList("admin"));

		RealmResource realmResource = kc.realm(realm);
		UsersResource userResource = realmResource.users();

//		UserRepresentation userRepresentation = kc.realm(realm).users().get("admin").toRepresentation();
		UserRepresentation userRepresentation = userResource.list().stream().filter(u -> u.getUsername().equals("user")).findFirst().orElse(null);
		userRepresentation.setEnabled(false);
//		userRepresentation.setEnabled(true);
		System.out.println(userRepresentation.isEnabled());
		userResource.get(userRepresentation.getId()).update(userRepresentation);

		// Create testuser
		Response result = kc.realm(realm).users().create(newUser);
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
		kc.realm(realm).users().get(userId).remove();
	}

}
