package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.ImportPostDTO;
import softuni.exam.instagraphlite.models.dtos.ImportPostRootDTO;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.models.entities.Post;
import softuni.exam.instagraphlite.models.entities.User;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PictureService pictureService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final Path path = Path.of("src/main/resources/files/posts.xml");
    private final StringBuilder sb;

    public PostServiceImpl(PostRepository postRepository, PictureService pictureService, UserService userService, ModelMapper modelMapper) throws JAXBException {
        this.postRepository = postRepository;
        this.pictureService = pictureService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.sb = new StringBuilder();
        JAXBContext context = JAXBContext.newInstance(ImportPostRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.postRepository.count() >0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        ImportPostRootDTO posts = (ImportPostRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return posts.getPosts().stream()
                .map(this::importPost).collect(Collectors.joining("\n"));
    }

    private String importPost(ImportPostDTO post) {
        Set<ConstraintViolation<ImportPostDTO>> validate = this.validator.validate(post);
        if (validate.isEmpty()){
            Picture picture = this.pictureService.findPictureByPath(post.getPicture().getPath());
            User user = this.userService.findUserByUsername(post.getUser().getUsername());
            if (picture!=null && user!=null){

                Post postToAdd = this.modelMapper.map(post,Post.class);
                postToAdd.setPicture(picture);
                postToAdd.setUser(user);
                this.postRepository.save(postToAdd);
                sb.append(String.format("Successfully imported Post, made by %s%n"
                ,postToAdd.getUser().getUsername()));
            }else{
                sb.append("Invalid post").append(System.lineSeparator());
            }

        }else{
            sb.append("Invalid post").append(System.lineSeparator());
        }
        return sb.toString();
    }
}
