DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS demande_de_role CASCADE;
DROP TABLE IF EXISTS inscription CASCADE;
DROP TABLE IF EXISTS session CASCADE;
DROP TABLE IF EXISTS evenement CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

DROP TYPE IF EXISTS statut_evenement;
DROP TYPE IF EXISTS statut_inscription;
DROP TYPE IF EXISTS statut_session;
DROP TYPE IF EXISTS statut_demande_de_role;

CREATE TYPE statut_evenement AS ENUM ('Confirmer', 'Annuler', 'En-attente');
CREATE TYPE statut_inscription AS ENUM ('Confirmer', 'Refuser', 'En_attente', 'Annuler');
CREATE TYPE statut_session AS ENUM ('Confirmer', 'Annuler', 'En_attente');
CREATE TYPE statut_demande_de_role AS ENUM ('Confirmer', 'Refuser', 'En_attente');

CREATE TABLE utilisateur
(
    utilisateur_id             VARCHAR(255) PRIMARY KEY,
    nom                        VARCHAR(100) NOT NULL,
    prenom                     VARCHAR(100) NOT NULL,
    email                      VARCHAR(100) NOT NULL,
    role                       VARCHAR(50)  NOT NULL,
    date_creation_utilisateurs TIMESTAMP    NOT NULL,
    photo_profil               VARCHAR(255)
);

CREATE TABLE evenement
(
    evenement_id            BIGSERIAL PRIMARY KEY,
    statut_evenement        VARCHAR(50)  NOT NULL,
    titre                   VARCHAR(150) NOT NULL,
    description             TEXT,
    localisation            VARCHAR(100) NOT NULL,
    date_debut_evenement    TIMESTAMP    NOT NULL,
    date_fin_evenement      TIMESTAMP    NOT NULL,
    duree_evenement         BIGINT       NOT NULL,
    date_creation_evenement TIMESTAMP    NOT NULL,
    utilisateur_id          VARCHAR(255) NOT NULL,
    CONSTRAINT fk_evenement_utilisateur_id FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur (utilisateur_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE session
(
    session_id            BIGSERIAL PRIMARY KEY,
    statut_session        VARCHAR(50)  NOT NULL,
    date_debut_session    TIMESTAMP    NOT NULL,
    date_fin_session      TIMESTAMP    NOT NULL,
    duree_session         BIGINT       NOT NULL,
    titre                 VARCHAR(150) NOT NULL,
    description           TEXT,
    lieu                  VARCHAR(100) NOT NULL,
    animateur             VARCHAR(150),
    capacite_max          INT          NOT NULL,
    date_creation_session TIMESTAMP    NOT NULL,
    evenement_id          BIGINT       NOT NULL,
    CONSTRAINT fk_session_evenement_id FOREIGN KEY (evenement_id)
        REFERENCES evenement (evenement_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE inscription
(
    inscription_id     BIGSERIAL PRIMARY KEY,
    statut_inscription statut_inscription NOT NULL,
    utilisateur_id     VARCHAR(255)       NOT NULL,
    session_id         BIGINT             NOT NULL,
    CONSTRAINT fk_inscription_utilisateur_id FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur (utilisateur_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_inscription_session_id FOREIGN KEY (session_id)
        REFERENCES session (session_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE demande_de_role
(
    demande_de_role_id     BIGSERIAL PRIMARY KEY,
    statut_demande_de_role statut_demande_de_role NOT NULL,
    utilisateur_id         VARCHAR(255),
    CONSTRAINT fk_demande_de_role_utilisateur_id FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur (utilisateur_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE notification
(
    notification_id BIGSERIAL PRIMARY KEY,
    titre           VARCHAR(100) NOT NULL,
    description     TEXT,
    date            TIMESTAMP,
    is_Lu           BOOLEAN DEFAULT FALSE,
    utilisateur_id  VARCHAR(255),
    evenement_id    BIGINT,
    session_id      BIGINT,
    CONSTRAINT fk_notification_utilisateur_id FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur (utilisateur_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_notification_evenement_id FOREIGN KEY (evenement_id)
        REFERENCES evenement (evenement_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_notification_session_id FOREIGN KEY (session_id)
        REFERENCES session (session_id) ON DELETE CASCADE ON UPDATE CASCADE
);

