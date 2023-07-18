package com.blog.service.impl;

import com.blog.entity.Post;
import com.blog.exception.ResourceNotFoundException;
import com.blog.payload.PostDto;
import com.blog.repository.PostRepository;
import com.blog.service.PostService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {


    private final PostRepository postRepository;

    private ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper) {
        this.postRepository=postRepository;
        this.modelMapper=modelMapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post=new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        Post newpost = postRepository.save(post);

//        PostDto dto=new PostDto();
//        dto.setId(newpost.getId());
//        dto.setTitle(newpost.getTitle());
//        dto.setDescription(newpost.getDescription());
//        dto.setContent(newpost.getContent());

        return mapToDto(newpost);

    }

    @Override
    public List<PostDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Sort sort = Sort.by(sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> listofposts = postRepository.findAll(pageable);

        List<Post> posts = listofposts.getContent();
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post not found with id: " + id)
        );

        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(long id, PostDto postDto) {
         postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post not found with id: " + id)
        );
        Post newpost = mapToEntity(postDto);
        newpost.setId(id);
        Post updatedPost = postRepository.save(newpost);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePost(long id) {
        postRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Post not found with id: "+id)
        );

        postRepository.deleteById(id);

    }


    PostDto mapToDto(Post post) {

//        PostDto dtos=new PostDto();
//        dtos.setId(post.getId());
//        dtos.setTitle(post.getTitle());
//        dtos.setDescription(post.getDescription());
//        dtos.setContent(post.getContent());

        PostDto dto = modelMapper.map(post, PostDto.class);
        return dto;
    }

    Post mapToEntity(PostDto postDto){
//        Post post=new Post();
//        post.setId(postDto.getId());
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());

        Post post = modelMapper.map(postDto, Post.class);
        return post;
    }

}
