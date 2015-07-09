from django.conf.urls import patterns, include, url
from django.contrib import admin
from django.conf import settings
from agriApp import views


urlpatterns = patterns('',
    url(r'^admin/', include(admin.site.urls)),
    url(r'^$', views.index, name='index'),
    url(r'^roads/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.roads, name='roads'),
    url(r'^rivers/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.rivers, name='rivers'),
    url(r'^contours/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.contours, name='contours'),
    url(r'^soilCapability/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.soilCapability, name='soilCapability'),
    url(r'^pipelines/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.pipelines, name='pipelines'),
 	url(r'^rainfall/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.rainfall, name='rainfall'),
    url(r'^landUse/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.lUse, name='lUse'),
    url(r'^recommendLettuce/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendLettuce, name='recommendLettuce'),
    url(r'^recommendTomato/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendTomato, name='recommendTomato'),
    url(r'^recommendBreadfruit/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendBreadfruit, name='recommendBreadfruit'),
    url(r'^recommendPapaya/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendPapaya, name='recommendPapaya'),
    url(r'^recommendCucumber/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendCucumber, name='recommendCucumber'),
    url(r'^recommendOnion/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendOnion, name='recommendOnion'),
    url(r'^recommendCorn/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendCorn, name='recommendCorn'),
    url(r'^recommendCitrus/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendCitrus, name='recommendCitrus'),
    url(r'^recommendPigeonPea/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendPigeonPea, name='recommendPigeonPea'),
    url(r'^recommendSweetPepper/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendSweetPepper, name='recommendSweetPepper'),
    url(r'^recommendPineapple/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendPineapple, name='recommendPineapple'),
    url(r'^recommendSweetPotato/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendSweetPotato, name='recommendSweetPotato'),
    url(r'^recommendCitrus/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendCitrus, name='recommendCitrus'),
    url(r'^parcels/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.parcel, name='parcel'),
    url(r'^cropSpecifications', views.cropSpecifications, name='cropSpecifications'),
    url(r'^soilAndRainfall/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.soilAndRainfall, name='soilAndRainfall'),
    url(r'^recommendAll/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))&(?P<radius>([-\d.]+))', views.recommendAll, name='recommendAll'),
    url(r'^closeObjects/(?P<lon>([-\d.]+))&(?P<lat>([-\d.]+))', views.closestObjects, name='closestObjects')
)
