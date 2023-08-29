
-- -- USER
-- INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
-- VALUES (gen_random_uuid(), false, true, false, 'Buea', 'Cameroon', CURRENT_TIMESTAMP, 'ndip.lawrence@email.gmail.com', gen_random_uuid(), 'Lawrence', 'English', 'Ndip', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000000', null, 'Southwest', 'USER-ECOMIEST', current_timestamp);

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('4770e7c1-77fc-4b6b-bf95-e01fc712e647', false, true, false, 'Buea', 'Cameroon', '2022-02-20T17:56:58+00:00', 'ndip.lawrence@email.gmail.com', 'f3371d4a-d81d-4965-bc0f-e9e5cb3b1ecc', 'Lawrence', 'English', 'Ndip', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000000', null, 'Southwest', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('358a9450-7675-4a39-b685-309f57dc9b78', false, true, false, 'Dschang', 'Cameroon', '2021-05-01T17:56:58+00:00', 'foufeu.Joel@email.gmail.com', '6edef2db-0db3-4aab-9f4a-709f05e39d6a', 'Joel', 'French', 'Foufeu', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000001', null, 'West', 'USER-ECOMIEST', '2023-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('31cf2545-e1e4-47fa-b925-ea5e8b494366', false, true, false, 'Douala', 'Cameroon', '2020-12-31T17:56:58+00:00', 'ndolo.idriss@email.gmail.com', '5ad1049a-af24-41b5-934c-40284f41720d', 'Idriss', 'English', 'Ndolo', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000002', null, 'Litoral', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('7ac4c0d5-6bfc-44ba-bcc9-6b417d47ba8b', false, true, false, 'Yaounde', 'Cameroon', '2023-04-21T17:56:58+00:00', 'ayissi.bernisse@email.gmail.com', 'd6df7fe0-f81f-417b-a527-46bf05087f95', 'Bernisse', 'French', 'Ayissi', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000003', null, 'Centre', 'USER-ECOMIEST', '2022-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('03addc11-1558-4839-9546-48a86e640d3a', false, true, false, 'Ngaoundere', 'Cameroon', '2022-02-20T17:56:58+00:00', 'adamou.chris@email.gmail.com', '5084b734-7ed3-404e-9ab9-d7b190dd98c7', 'Chris', 'English', 'Adamou', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000005', null, 'Adamawa', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('554869d9-d9e6-4f00-b359-aa93ba25f254', false, true, false, 'Bertoua', 'Cameroon', '2021-05-01T17:56:58+00:00', 'mbengue.michou@email.gmail.com', '914bd16f-cc0f-42d3-ac7e-1a132b5674d4', 'Michou', 'French', 'Mbengue', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000006', null, 'East', 'USER-ECOMIEST', '2023-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('8ccdc0b6-a880-47f3-a4f4-d3e60bf1a349', false, true, false, 'Bamenda', 'Cameroon', '2020-12-31T17:56:58+00:00', 'nfor.elvis@email.gmail.com', '64e6c3f8-e3cf-4aea-9497-76472f79cd3c', 'Elvis', 'English', 'Nfor', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000007', null, 'Northwest', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('3bca4147-3a48-468f-aca8-1489a8db849c', false, true, false, 'Maroua', 'Cameroon', '2023-04-21T17:56:58+00:00', 'sarifou.mohamed@email.gmail.com', '0eaffe9e-660c-458b-911b-f0440f7473d0', 'Mohamed', 'French', 'Sarifou', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000004', null, 'Far North', 'USER-ECOMIEST', '2022-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('5d532739-0409-4e30-9abc-96cc61b20f3f', false, true, false, 'Kumba', 'Cameroon', '2022-02-20T17:56:58+00:00', 'lobe.queen@email.gmail.com', '41f6905c-6e46-4bb0-8ccd-1e31e33e32f5', 'Queen', 'English', 'Lobe', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000008', null, 'Southwest', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('8d9b1adc-a75b-433d-8438-079df77626ba', false, true, false, 'Balengou Centre', 'Cameroon', '2021-05-01T17:56:58+00:00', 'donfack.christelle@email.gmail.com', '6e00ac61-b6b5-4b6a-8aab-74d3e08e5970', 'Christelle', 'French', 'Donfack', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000009', null, 'West', 'USER-ECOMIEST', '2023-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('a395286a-7f83-43b8-9b48-4b15b2d9cd95', false, true, false, 'Kongsamba', 'Cameroon', '2020-12-31T17:56:58+00:00', 'elong.junior@email.gmail.com', '3d11712d-cbee-452f-bc52-c4fd1b241180', 'Junior', 'English', 'Elong', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000010', null, 'Litoral', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('0015d7b7-0a91-4812-9804-22cf17568278', false, true, false, 'Yaounde', 'Cameroon', '2023-04-21T17:56:58+00:00', 'ondoua.jaque@email.gmail.com', 'ba2f2446-4610-429f-8649-9b98959b9a18', 'Jaque', 'French', 'Ondoua', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000011', null, 'Centre', 'USER-ECOMIEST', '2022-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('a145d88d-6850-4052-a1ba-3d1f28991998', false, true, false, 'Ngaoundere', 'Cameroon', '2022-02-20T17:56:58+00:00', 'hamanna.ali@email.gmail.com', '07cc3559-a537-4bfd-afd6-00179cc12d70', 'Ali', 'English', 'Hamana', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000012', null, 'Adamawa', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('5a09ea0a-9943-416b-a80a-84b8975e12aa', false, true, false, 'Bertoua', 'Cameroon', '2021-05-01T17:56:58+00:00', 'bengan.karl@email.gmail.com', '456006ec-f097-4a5b-a086-abff3966d7c4', 'Karl', 'French', 'Bengan', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000013', null, 'East', 'USER-ECOMIEST', '2023-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('439e2926-2e0f-418f-b317-b2053754081b', false, true, false, 'Bamenda', 'Cameroon', '2020-12-31T17:56:58+00:00', 'nkonyui.elizabeth@email.gmail.com', '6d1afa57-25d8-4abb-8491-fce2ef6369ce', 'Elizabeth', 'English', 'Nkonyui', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000014', null, 'Northwest', 'USER-ECOMIEST', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public._user(id, account_blocked, account_enabled, account_soft_deleted, city, country, created_at, email, email_verification_token, firstname, language, lastname, password, phone_number, profile_picture_file_name, region, roles, updated_at)
VALUES ('ac2e322a-eb51-47ce-b88a-031095cbd983', false, true, false, 'Maroua', 'Cameroon', '2023-04-21T17:56:58+00:00', 'dadanna.samira@email.gmail.com', 'dd373c8b-bfed-49fc-b752-873ee4d37b98', 'Samira', 'French', 'Dadanna', '$2a$12$rB7r.BGcF47tT1OEwgOn6O6umzdGyfEp3pOhRdTwvuyATQcC6BsSS', '+237670000015', null, 'Far North', 'USER-ECOMIEST', '2022-08-02T17:30:58+00:00') ON DUPLICATE KEY UPDATE;

-- SESSION
INSERT INTO public.session(id, created_at, description, end_date, name, start_date, status, updated_at)
VALUES ('0f799c5c-6ebd-49f8-aeac-86f65c266c0d', '2023-07-21T17:56:58+00:00', 'This is a test session 1', '2023-12-21T17:56:58+00:00', '2022 - 2023 Session', '2023-08-21T17:56:58+00:00', 'ONGOING', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public.session(id, created_at, description, end_date, name, start_date, status, updated_at)
VALUES ('16329109-ee7a-4637-96f3-b4b9add32572', '2023-07-21T17:56:58+00:00', 'This is a test session 2', '2024-12-21T17:56:58+00:00', '2022 - 2023 Session II', '2024-01-01T17:56:58+00:00', 'INACTIVE', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;


-- CHALLENGE
INSERT INTO public.challenge(id, created_at, description, name, target, type, updated_at)
VALUES ('e0735546-441c-44cf-bbb5-64b9e33e9bf7', '2023-07-21T17:56:58+00:00', 'Evangelize to FIVE (5) persons per week', '5 Per Week', 5, 'NORMAL', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public.challenge(id, created_at, description, name, target, type, updated_at)
VALUES ('b52d227d-8ea5-492c-b5cb-16d747043231', '2023-07-21T17:56:58+00:00', 'Evangelize to THREE (3) persons per week', '3 Per Week', 3, 'NORMAL', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public.challenge(id, created_at, description, name, target, type, updated_at)
VALUES ('0eaffe9e-660c-458b-911b-f0440f7473d0', '2023-07-21T17:56:58+00:00', 'Evangelize to ONE (1) person per week', '1 Per Week', 1, 'NORMAL', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;

INSERT INTO public.challenge(id, created_at, description, name, target, type, updated_at)
VALUES ('3af6e2d2-20e4-4a9c-b291-7a73026afc9c', '2023-07-21T17:56:58+00:00', 'Evangelize to a 100 persons in 3 weeks', 'Ketsinami Challenge', 100, 'EVENT', '2023-08-21T17:56:58+00:00') ON DUPLICATE KEY UPDATE;