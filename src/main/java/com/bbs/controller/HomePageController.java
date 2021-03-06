package com.bbs.controller;

import com.bbs.entity.Information;
import com.bbs.entity.Post;
import com.bbs.entity.User;
import com.bbs.mapper.InformationMapper;
import com.bbs.service.PostService;
import com.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName HomePageController
 * @Description 网站首页设计
 * @Author ZengChun
 * @Date 2019/12/15 18:15
 **/
@Controller
public class HomePageController {
    @Autowired
    public PostService postService;
    @Autowired
    public UserService userService;
    @Autowired
    public InformationMapper informationMapper;
    //首页URL通用：localhost：8080，根据所传参数来判断跳转到哪个页面
    @GetMapping({"/","/index.html"})
    public String indexPost(Model model,
                            Map<String,Object> map,
                            HttpSession session,
                            @RequestParam(name = "postType",required = false,defaultValue ="1" )Integer postType,
                            @RequestParam(name = "indexType",required = false,defaultValue = "1")Integer indexType,
                            @RequestParam(name="pageNumber" ,required = false,defaultValue = "1") Integer pageNumber,
                            @RequestParam(name = "signIn",required = false,defaultValue = "0")int signIn){
        //签到
        if (signIn!=0){
            User user = userService.selectByTel(session.getAttribute("tel").toString());
            user.setIntegral(user.getIntegral() + 10);
            user.setReputationValue(user.getReputationValue() + 1);
            userService.updateInformation(user);
            session.setAttribute("signNum","success");
        }
        //登录成功后显示签到区的头像
        if(session.getAttribute("tel")!=null) {
            User nowUser = userService.selectByTel(session.getAttribute("tel").toString());
            map.put("userHead", nowUser.getHead());
        }

        map.put("indexType",indexType);
        map.put("postType",postType);
        map.put("page",pageNumber);
        List<Post> newPosts = null;
        if(postType==1) {
            //左边页面显示的内容，"首页"
            if (indexType == 1) {
                //左边页面显示的内容，查询所有帖子，并按时间排序--》“最新”
                newPosts = postService.findAllByPage(1,pageNumber,12);
            } else {
                //左边页面显示的内容，查询所有置顶帖子，并按时间排序--》“置顶”
                newPosts = postService.findAllByPage(2,1,9);
            }
        }else if(postType==2){
            //左边页面显示的内容，"精品帖区"
            //左边页面显示的内容，查询所有加精帖子，并按时间排序--》“最新”
            newPosts = postService.findAllByPage(3,pageNumber,12);
        } else if(postType==3){
            //左边页面显示的内容，"需求帖区"
            //左边页面显示的内容，查询所有需求帖子，并按时间排序--》“最新”
            newPosts = postService.findAllByPage(4,pageNumber,12);
        }else if(postType==4){
            //左边页面显示的内容，"人气排行"
            //左边页面显示的内容，查询所有帖子，并按点赞数排序--》“最新”
            newPosts = postService.findAllByPage(6,1,10);
            model.addAttribute("Post",newPosts);
        }else if(postType==5){
            //左边页面显示的内容，"积分排行"
            //左边页面显示的内容，查询所有用户，并按积分排序--》“最新”
            List<User> users0 =userService.selectAll() ;
            List<User> users1= users0.stream().sorted((a, b) -> b.getIntegral() - a.getIntegral()).collect(Collectors.toList());
            model.addAttribute("user", users1);
        }else if(postType==6){
            //左边页面显示的内容，"天健园"
            //左边页面显示的内容，查询所有天健园帖子，并按时间排序--》“最新”
            newPosts = postService.findAllByPage(7,pageNumber,12);
        }else if(postType==7){
            //左边页面显示的内容，"休闲区"
            //左边页面显示的内容，查询所有休闲区帖子，并按时间排序--》“最新”
            newPosts = postService.findAllByPage(8,pageNumber,12);
        }else if(postType==8){
            //左边页面显示的内容，"医学院"
            //左边页面显示的内容，查询所有医学院帖子，并按时间排序--》“最新”
            newPosts = postService.findAllByPage(9,pageNumber,12);
        }

        //右边页面显示的内容，查询浏览量最高的前9条帖子，在本周热议栏展示
        List<Post> hotMostPost=postService.findAllByPage(6,1,9);
        model.addAttribute("hotPost",hotMostPost);
        //右边页面显示的内容，查询点赞数最高的前6条帖子，在本周热点栏展示
        List<Post> popularMostPost=postService.findAllByPage(5,1,6);
        model.addAttribute("popularPost",popularMostPost);

        List<Post> posts = newPosts;
        List<User> posters = new ArrayList<>();
        Map<Post, User> postUserMap = new LinkedHashMap<>();

        if(posts!=null) {
            for (Post post : posts) {
                User user = userService.selectByTel(post.getPosterID());
                posters.add(user);
            }
            for (int i = 0; i < posts.size(); i++) {
                postUserMap.put(posts.get(i), posters.get(i));
            }
            model.addAttribute("postUserMap", postUserMap);
        }
        //加载首页
        return "index";
    }

    //点击首页上某个帖子的标题时，获取该帖子ID，并跳转到该帖子的详细界面
    @GetMapping("/toPost")
    public String toPostPage(@RequestParam("postId") int postId, Model model,Map<String,Object> map,HttpSession session){
        map.put("postId",postId);
        //显示大头像
        if(session.getAttribute("tel")!=null) {
            User nowUser = userService.selectByTel(session.getAttribute("tel").toString());
            map.put("userHead", nowUser.getHead());
        }
        //查询该postID对应的帖子
        Post post=postService.findByPostID(postId);
        model.addAttribute("post",post);
        User theUser=userService.selectByTel(post.getPosterID());
        map.put("theHead",theUser.getHead());
        //查询该postID相应的评论
        List<Post> comments=postService.findPostByMainID(postId);
        model.addAttribute("comments",comments);
        List<User> commentUser= new ArrayList<>();
        Map<Post, User> commentUserMap = new LinkedHashMap<>();

        if(comments!=null) {
            for (Post comment : comments) {
                User user = userService.selectByTel(comment.getPosterID());
                commentUser.add(user);
            }
            for (int i = 0; i < comments.size(); i++) {
                commentUserMap.put(comments.get(i), commentUser.get(i));
            }
        }
        model.addAttribute("commentAndUser",commentUserMap);

        //查询每一条评论对应的所有回复
        List<List<Post>> addToComment= new ArrayList<>();
        Map<Post,List<Post>> commentsAndComment=new LinkedHashMap<>();
        if(comments!=null){
            for(Post comment:comments){


                addToComment.add(postService.findAnswerByParentID(comment.getPostID()));
            }
            for(int i=0;i<comments.size();++i){
                commentsAndComment.put(comments.get(i), addToComment.get(i));
            }
            model.addAttribute("answers",commentsAndComment);
        }

        //右边页面显示的内容，查询浏览量最高的前9条帖子，在本周热议栏展示
        List<Post> hotMostPost=postService.findAllByPage(6,1,9);
        model.addAttribute("hotPost",hotMostPost);
        //右边页面显示的内容，查询点赞数最高的前6条帖子，在本周热点栏展示
        List<Post> popularMostPost=postService.findAllByPage(5,1,6);
        model.addAttribute("popularPost",popularMostPost);
        //每点开一次浏览量加1
        post.setPageView(post.getPageView() + 1);
        //点击首页帖子标题跳转到该帖子详细界面
        postService.updatePost(post);
        return "tiezi";
    }

    //帖子页面点击发表评论
    @PostMapping("/writeComment")
    public String addCommit(@RequestParam("comment") String comment,
                            @RequestParam("postId") int postId,
                            HttpSession session){
        if(session.getAttribute("username")==null){
            return "/login.html";
        }
        //新建一个评论对象
        Post newComment=new Post();
        newComment.setPosterID(session.getAttribute("tel").toString());
        newComment.setPosterName(session.getAttribute("username").toString());
        newComment.setMainPost(postId);
        newComment.setPostTime(new Timestamp((new Date()).getTime()));
        newComment.setPostContent(comment);
        User user2=userService.selectByTel(session.getAttribute("tel").toString());
        newComment.setImageAddress(user2.getHead());
        postService.addPost(newComment);//将新建的评论对象存入帖子表

        //根据主帖号查询帖子信息
        Post post=postService.findByPostID(postId);

        //新建一个消息对象
        Information information=new Information();
        information.setReceiverTel(post.getPosterID());       //接收方手机号
        information.setPostTime(new Timestamp(new Date().getTime()));   //发送时间
        information.setOriginTitle(post.getPostTitle());        //原帖标题
        information.setPosterID(session.getAttribute("tel").toString());      //消息发送人id
        information.setPostID(String.valueOf(postId));          //回应的帖子的id
        informationMapper.addInformation(information);      //将新消息存入信息表
        return "redirect:/toPost?postId="+postId;
    }

    //评论回复
    @PostMapping("/answerComment")
    public String answerComment(@RequestParam("answerContent") String answerComment,
                                @RequestParam("motherID") int postId,
                                @RequestParam("parentID") int parentID,
                                HttpSession session){
        if(session.getAttribute("username")==null){
            return "/login.html";
        }
        Post newAnswer=new Post();
        newAnswer.setPosterID(session.getAttribute("tel").toString());
        newAnswer.setPosterName(session.getAttribute("username").toString());
        newAnswer.setMainPost(postId);
        newAnswer.setParentID(parentID);
        newAnswer.setPostTime(new Timestamp((new Date()).getTime()));
        newAnswer.setPostContent(answerComment);
        postService.addPost(newAnswer);//将新建的回复对象存入帖子表
        return "redirect:/toPost?postId="+postId;
    }

    //点击搜素按钮,根据输入关键字进行模糊查询；
    @GetMapping("/search")
    public String toSearch(@RequestParam("keyword") String keyword, Model model, Map<String,Object> map){
        map.put("keyword",keyword);
        List<Post> likelyPosts=postService.findLikePostTitle(keyword);
        List<Post> lPosts = likelyPosts;
        List<User> lPosters = new ArrayList<>();
        Map<Post, User> lPostUserMap = new LinkedHashMap<>();
        model.addAttribute("likelyPost",likelyPosts);
        if(lPosts!=null) {
            for (Post lPost : lPosts) {
                User lUser = userService.selectByTel(lPost.getPosterID());
                lPosters.add(lUser);
            }
            for (int i = 0; i < lPosts.size(); i++) {
                lPostUserMap.put(lPosts.get(i), lPosters.get(i));
            }
            model.addAttribute("lPostUserMap", lPostUserMap);
        }
        //来到搜索页面
        return "search";
    }

    //点击签到按钮
    @GetMapping("/addIntegral")
    public String addIntegral(HttpSession session){
        return "redirect:/";
    }
    //点赞
    @GetMapping("/giveLike")
    public String giveALike(@RequestParam("postId") int postId,HttpSession session){
        Post post=postService.findByPostID(postId);
        post.setLikeNumber(post.getLikeNumber()+1);
        postService.updatePost(post);
        session.setAttribute("giveALike","success");
        return "redirect:/toPost?postId="+postId;
    }
    //    积分
    @RequestMapping(value = "/tiezi/cn",method = RequestMethod.GET)
    public String cn(Map<String,Object> map,HttpServletRequest request){
        User user= userService.selectByTel(request.getParameter("tel"));
        int id = Integer.parseInt(request.getParameter("id"));
        int jf = postService.findByPostID(id).getPostIntegral();    //积分
        Post post = postService.findByPostID(id);
        post.setPostIntegral(0);
        map.put("cn","已采纳！");
        user.setIntegral(user.getIntegral()+jf);
        postService.updatePost(post);
        userService.updateInformation(user);
        System.out.println( postService.findByPostID(id).getPostIntegral());
        System.out.println(jf);
        System.out.println(666666);
        return "redirect:/toPost?postId="+id;
    }



    //删除评论贴子
    @GetMapping("/deleteComment")
    public String deletePost(@RequestParam("id") int id,
                             @RequestParam("postId") int postId,
                             Model model,
                             Map<String,Object> map){
        postService.deleteByPostID(id);
        map.put("postId",postId);
        System.out.println(postId);
        //查询该postID对应的帖子
        Post post=postService.findByPostID(postId);
        model.addAttribute("post",post);
        //查询该postID相应的评论
        List<Post> comments=postService.findPostByMainID(postId);
        model.addAttribute("comments",comments);
        //右边页面显示的内容，查询浏览量最高的前9条帖子，在本周热议栏展示
        List<Post> hotMostPost=postService.findAllByPage(6,1,9);
        model.addAttribute("hotPost",hotMostPost);
        //右边页面显示的内容，查询点赞数最高的前6条帖子，在本周热点栏展示
        List<Post> popularMostPost=postService.findAllByPage(5,1,6);
        model.addAttribute("popularPost",popularMostPost);
        //点击首页帖子标题跳转到该帖子详细界面
        post.setPageView(post.getPageView() + 1);
        postService.updatePost(post);
        return "redirect:/toPost?postId="+postId;

    }

}
