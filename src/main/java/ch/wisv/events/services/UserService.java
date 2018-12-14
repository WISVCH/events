package ch.wisv.events.services;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.repository.UserRepository;
import javax.transaction.Transactional;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
     * @param publisher      of type ApplicationEventPublisher
     * @param userRepository of type UserRepository
     */
    @Autowired
    public UserService(ApplicationEventPublisher publisher, UserRepository userRepository) {
        super(publisher, userRepository);
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
     * Create a User by CHUserInfo
     *
     * @param userInfo of type CHUserInfo
     *
     * @return User
     */
    public User createByChUserInfo(CHUserInfo userInfo) {
        User user = new User(userInfo.getSub(), userInfo.getName(), userInfo.getEmail());
        this.save(user);

        return user;
    }

    /**
     * Something to do before the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void beforeSave(User model) {

    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(User model) {
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
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(User model) {
    }

    /**
     * Set sub to null if it is left empty.
     *
     * @param model of type User
     */
    private void setSubToNullIfEmpty(User model) {
        if (isEmpty(model.getSub())) {
            model.setSub(null);
        }
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
        this.setSubToNullIfEmpty(model);

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
        this.setSubToNullIfEmpty(model);

        return model;
    }
}
