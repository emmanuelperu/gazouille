# gazouille
Android application connected to a private twitter like platform

# Configuration
Ajouter dans le fichier ~/.gradle/gradle.properties les variables suivantes :
GAZOUILLE_FIREBASE_URL
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET

Ajouter dans les Security & Rules dans Firebase ceci :
{
    "rules": {
      "tweets": {
          ".read": true,
          ".write": "auth !== null && auth.provider === 'password'"
      },
      "hashtags": {
        ".read": "auth !== null && auth.provider === 'password'",
        ".write": "auth !== null && auth.provider === 'password'"
      },
      "users" : {
        ".read": true,
        ".write": "auth !== null && auth.provider === 'password'"
      }
    }
}