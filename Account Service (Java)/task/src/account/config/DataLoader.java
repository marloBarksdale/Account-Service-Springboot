package account.config;

import account.dto.Group;
import account.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private GroupRepository groupRepository;


    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("administrator", "ROLE_ADMINISTRATOR"));
            groupRepository.save(new Group("user", "ROLE_USER"));
            groupRepository.save(new Group("accountant", "ROLE_ACCOUNTANT"));
            groupRepository.save(new Group("auditor", "ROLE_AUDITOR"));
        } catch (Exception e) {

        }
    }
}