# MyCache
redis缓存注解，也支持分页缓存，分页缓存注销，小粒度缓存

## 开启缓存
mycache:
enable: true

## 使用缓存
@Override  
@MyCache(service = MyCacheInfo.SERVICE, module = MyCacheInfo.MODULE, method = "openPageNews")    
public CommonResult<Page<News>> openPageNews(Integer pageNum, Integer pageSize){}  

## 失效多个缓存
@Override
@MyCacheInvalidate(keys = {  
MyCacheInfo.SERVICE + MyCacheInfo.MODULE + "openPageNews",  
MyCacheInfo.SERVICE + MyCacheInfo.MODULE + "getNewsById"  
})  
public CommonResult<Boolean> updateNews(News req) {}

