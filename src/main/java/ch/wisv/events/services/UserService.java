package ch.wisv.events.services;

import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.repository.UserRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserService class.
 */
@Service
@Transactional
public class UserService extends AbstractService<User> {

    /**
     * UserRepository constructor.
     *
     * @param userRepository of type UserRepository
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected User create(User model) {
        return model;
    }

    /**
     * Update of an AbstractModel.
     *
     * @param model         of type AbstractModel
     * @param existingModel of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected User update(User model, User existingModel) {
        return model;
    }
}
