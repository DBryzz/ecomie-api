package org.csbf.security.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.csbf.security.constant.Role;
import org.csbf.security.model.User;
import org.csbf.security.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final Environment env;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!userRepo.existsByEmail(env.getProperty("super.admin.email"))) {
            User superAdminUser = User.builder()
                    .email(env.getProperty("super.admin.email"))
                    .firstname(env.getProperty("super.admin.first-name"))
                    .lastname(env.getProperty("super.admin.last-name"))
                    .phoneNumber(env.getProperty("super.admin.phone-number"))
                    .password(passwordEncoder.encode(env.getProperty("super.admin.password")))
                    .roles(Role.SUPER_ADMIN.name()+"-"+Role.ADMIN.name()+"-"+Role.ECOMIEST.name()+"-"+Role.MISSIONARY.name()+"-"+Role.PRAYER_WARRIOR.name()+"-"+Role.SPONSOR.name()+"-"+Role.USER.name())
                    .emailVerificationToken(env.getProperty("SUPER_ADMIN_EMAIL_VERIFICATION_TOKEN"))
                    .country(env.getProperty("super.admin.country"))
                    .region(env.getProperty("super.admin.region"))
                    .city(env.getProperty("super.admin.city"))
                    .language(env.getProperty("super.admin.language"))
                    .accountBlocked(false)
                    .accountEnabled(true)
                    .accountSoftDeleted(false)
                    .build();
            userRepo.save(superAdminUser);
        }
    }
}
