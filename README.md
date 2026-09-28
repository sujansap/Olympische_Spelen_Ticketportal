# Olympische Spelen

Spring Boot-webapplicatie voor het bekijken van sporten en wedstrijden van de Olympische Spelen en het kopen van tickets.

## Functionaliteit

- Overzicht van sporten en wedstrijden, met detailpagina per wedstrijd
- Tickets kopen voor een wedstrijd
- Inloggen en rollen met Spring Security
- Wedstrijden bewerken
- REST-API voor wedstrijden
- Validatie (o.a. wedstrijddatum en Olympisch nummer)
- Foutpagina's (403, niet gevonden)

## Technologieën

- Java
- Spring Boot
- Spring Security
- Thymeleaf
- REST
- Tests met gemockte controllers

## Structuur

```
src/main/java
├── com.springBoot.olympischeSpelen   # controllers en configuratie
├── domain                            # entiteiten (Sport, Stadium, Ticket, Wedstrijd, ...)
├── repository                        # databanktoegang
├── service                           # businesslogica
├── validator                         # eigen validatie
├── exception                         # eigen excepties
├── utility                           # serializers en hulpklassen
└── web                               # interceptor

src/main/resources
├── templates                         # Thymeleaf-pagina's
├── static/css                        # stijlen
└── i18n                              # berichten

src/test/java                         # controllertests
```

## Starten

Start `SpringBootOlympischeSpelenApplication` vanuit je IDE. Instellingen staan in `src/main/resources/application.properties`.
