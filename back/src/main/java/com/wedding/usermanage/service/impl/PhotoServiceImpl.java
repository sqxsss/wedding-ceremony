package com.wedding.usermanage.service.impl;

import com.wedding.mapper.AlbumMapper;
import com.wedding.mapper.Album_photoMapper;
import com.wedding.mapper.UserMapper;
import com.wedding.model.ReturnMessage;
import com.wedding.model.po.Album;
import com.wedding.model.po.Album_photo;
import com.wedding.model.po.User;
import com.wedding.usermanage.service.PhotoService;
import com.wedding.usermanage.utils.CosClient;
import com.wedding.usermanage.vo.AlbumVO;
import com.wedding.usermanage.vo.PhotoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;

@Service("photoService")
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private Album_photoMapper album_photoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    @Transactional
    public ReturnMessage uploadPhotos(int userid,MultipartFile[] files) {
        String fileName = null;
        User user=userMapper.selectByPrimaryKey(userid);
        Album album=albumMapper.selectByPrimaryKey(user.getAlbumid());
        if(album.getMaxNumber()-album.getCurrentNumber()<files.length){
            return new ReturnMessage(false,"相册所余空间不足，上传失败！");
        }
        for(int i=0;i<files.length;i++) {
            MultipartFile filecontent=files[i];

            album.setCurrentNumber(album.getCurrentNumber()+1);
            albumMapper.updateByPrimaryKey(album);

            //向数据库存入路径
            Album_photo album_photo=new Album_photo();
            album_photo.setAddress("/album/"+userid);
            album_photo.setUploadTime(new Date());
            album_photo.setAlbumid(album.getId());
            album_photo.setOrderNumber(album.getCurrentNumber());
            album_photoMapper.insert(album_photo);
            fileName = filecontent.getOriginalFilename();
            String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
            fileName=album_photo.getId()+"."+suffix;
            try {
                File file=File.createTempFile(album_photo.getId()+"",suffix);
                filecontent.transferTo(file);
                CosClient.uploadFile("album/"+userid+"/"+fileName,file);
            } catch (IOException e) {
                e.printStackTrace();
            }

                album_photo.setAddress(album_photo.getAddress()+"/"+fileName);
                album_photoMapper.updateByPrimaryKey(album_photo);
        }
        return new ReturnMessage(true,"上传成功");
    }

    @Override
    public AlbumVO getAlbum(int userid) {
        User user=userMapper.selectByPrimaryKey(userid);
        Album album=albumMapper.selectByPrimaryKey(user.getAlbumid());
        List<Album_photo> photos=album_photoMapper.selectByAlbumId(user.getAlbumid());
        PhotoVO[] photoVOS=new PhotoVO[photos.size()];
        AlbumVO albumVO=new AlbumVO();
        albumVO.setAlbumid(album.getId());
        albumVO.setCurrentNumber(album.getCurrentNumber());
        albumVO.setMaxNumber(album.getMaxNumber());
        for(int i=0;i<photos.size();i++){
            for(int j=0;j<photos.size();j++){
                if(photos.get(j).getOrderNumber()==(i+1)){
                    PhotoVO photoVO=new PhotoVO();
                    photoVO.setUrl(CosClient.bucket_url+photos.get(j).getAddress());
                    photoVO.setOrder_number(photos.get(j).getOrderNumber());
                    photoVO.setId(photos.get(j).getId());
                    photoVOS[i]=photoVO;
                    break;
                }
            }
        }
        albumVO.setPhotos(photoVOS);
        return albumVO;
    }

    @Override
    public ReturnMessage changePhotoOrder(PhotoVO photoVO) {
        Album_photo album_photo1=album_photoMapper.selectByPrimaryKey(photoVO.getId());
        List<Album_photo>photos=album_photoMapper.selectByAlbumId(album_photo1.getAlbumid());
        for(int i=0;i<photos.size();i++){
            if(photos.get(i).getOrderNumber()==photoVO.getOrder_number()){
                Album_photo album_photo2=photos.get(i);
                album_photo2.setOrderNumber(album_photo1.getOrderNumber());
                album_photo1.setOrderNumber(photoVO.getOrder_number());
                album_photoMapper.updateByPrimaryKey(album_photo1);
                album_photoMapper.updateByPrimaryKey(album_photo2);
            }
        }

        return new ReturnMessage(true,"修改成功！");
    }

    @Override
    public ReturnMessage deletePhoto(PhotoVO photoVO) {
        Album_photo album_photo=album_photoMapper.selectByPrimaryKey(photoVO.getId());
        List<Album_photo> photos=album_photoMapper.selectByAlbumId(album_photo.getAlbumid());
        for(int i=0;i<photos.size();i++){
            Album_photo album_photo1=photos.get(i);
            if(album_photo1.getOrderNumber()>album_photo.getOrderNumber()){
                album_photo1.setOrderNumber(album_photo1.getOrderNumber()-1);
                album_photoMapper.updateByPrimaryKey(album_photo1);
            }
        }
        Album album=albumMapper.selectByPrimaryKey(album_photo.getAlbumid());
        album.setCurrentNumber(album.getCurrentNumber()-1);
        albumMapper.updateByPrimaryKey(album);
        CosClient.deleteFile(album_photo.getAddress());
        album_photoMapper.deleteByPrimaryKey(album_photo.getId());
        return new ReturnMessage(true,"删除成功！");
    }


}
