package ch.wisv.events.services;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.repository.UserRepository;
import javax.transaction.Transactional;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserService class.
 */
@Service
@Transactional
public class UserService extends AbstractService<User> {

    /**
     * UserRepository.
     */
    private final UserRepository userRepository;

    /**
     * UserRepository constructor.
     *
     * @param userRepository of type UserRepository
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    /**
     * Get a user by email.
     *
     * @param email of type String
     *
     * @return String
     */
    public User getByEmail(String email) {
        return userRepository.getByEmail(email).orElseThrow(() -> new ModelNotFoundException(User.class, email));
    }

    /**
     * Get a user by sub.
     *
     * @param sub of type String
     *
     * @return String
     */
    public User getBySub(String sub) {
        return userRepository.getBySub(sub).orElseThrow(() -> new ModelNotFoundException(User.class, sub));
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(User model) {
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
        if (isEmpty(model.getSub())) {
            model.setSub(null);
        }

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
        if (isEmpty(model.getSub())) {
            model.setSub(null);
        }

        return model;
    }
}
