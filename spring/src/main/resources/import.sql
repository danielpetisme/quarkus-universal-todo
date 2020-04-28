DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START 1;

INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Introduction to Quarkus', true, 0, 'daniel');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Hibernate with Panache', false, 1, 'daniel');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Visit Quarkus web site', false, 2, 'daniel');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Star Quarkus project', false, 3, 'daniel');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Introduction to Quarkus', true, 0, 'alice');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Hibernate with Panache', false, 1, 'alice');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Visit Quarkus web site', false, 2, 'alice');
INSERT INTO todo(id, title, completed, ordering, owner) VALUES (nextval('hibernate_sequence'), 'Star Quarkus project', false, 3, 'alice');