from django.http import HttpResponse
from vectorformats.Formats import Django, GeoJSON
from django.contrib.gis.shortcuts import render_to_kml,render_to_kmz
from django.contrib.gis.measure import D
from django.contrib.gis.geos import *
from django.http import JsonResponse
from models import Roads,Soils,Rivers,Contours,Pipelines,RainfallData,soilFeatures,soilsAndRainfall,landUse,landParcel
import json
import time
import os

# Create your views here.

templateFolder = os.getcwd() + "/agriApp/templates"

def index(request):
    return HttpResponse("AgriMaps, default response.")

def roads(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	queryset = Roads.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.kml()
	return render_to_kmz(templateFolder + "/roads.kml", {'roads' : qsKML})

def closestObjects(request,lat=0,lon=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(10000)/1000)
	distance_degrees = distance_km/111.317
	querysetRoads = Roads.objects.filter(geomData__dwithin=(origin, distance_degrees))
	roadPoint = querysetRoads.distance(origin).order_by('distance')[0]
	querysetRivers = Rivers.objects.filter(geomData__dwithin=(origin, distance_degrees))
	riverPoint = querysetRivers.distance(origin).order_by('distance')[0]
	disObjects = {"road":roadPoint.distance.standard,"river":riverPoint.distance.standard}
	return JsonResponse(disObjects)	

def parcel(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	circle = origin.buffer((float(radius)/1000)/111.317)
	queryset = landParcel.objects.filter(geom__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/parcels.kml", {'parcels' : queryset})

def rainfall(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	circle = origin.buffer((float(radius)/1000)/111.317)
	queryset = RainfallData.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.intersection(circle).kml()
	return render_to_kmz(templateFolder + "/rainfall.kml", {'places' : qsKML})

def lUse(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.1
	distance_degrees = distance_km/111.317
	circle = origin.buffer((float(radius)/1000)/111.317)
	queryset = landUse.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.intersection(circle).kml()
	return render_to_kmz(templateFolder + "/landUse.kml", {'places' : qsKML})

def rivers(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	queryset = Rivers.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.kml()
	return render_to_kmz(templateFolder + "/rivers.kml", {'places' : qsKML})

def contours(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	queryset = Contours.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.kml()
	return render_to_kmz(templateFolder + "/contours.kml", {'places' : qsKML})

def pipelines(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)
	distance_degrees = distance_km/111.317
	queryset = Pipelines.objects.filter(geomData__dwithin=(origin, distance_degrees))
	qsKML = queryset.kml()
	return render_to_kmz(templateFolder + "/pipelines.kml", {'places' : qsKML})

def soilCapability(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = Soils.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/soilCapability.kml", {'places' : queryset})

def recommendLettuce(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendLettuce.kml", {'places' : queryset})

def recommendTomato(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendTomato.kml", {'places' : queryset})

def recommendBreadfruit(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendBreadfruit.kml", {'places' : queryset})

def recommendPapaya(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendPapaya.kml", {'places' : queryset})

def recommendCucumber(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	soilandrainQs = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendCucumber.kml", {'places' : soilandrainQs})

def recommendOnion(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	soilandrainQs = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendOnion.kml", {'places' : soilandrainQs})

def recommendCitrus(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendCitrus.kml", {'places' : queryset})

def recommendCorn(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendCorn.kml", {'places' : queryset})
	
def recommendPigeonPea(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendPigeonPea.kml", {'places' : queryset})

def recommendSweetPepper(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendSweetPepper.kml", {'places' : queryset})

def recommendPineapple(request,lat=0,lon=0,radius=0):
        origin = Point(float(lon),float(lat))
        distance_km = (float(radius)/1000)+0.01
        circle = origin.buffer((float(radius)/1000)/111.317)
        distance_degrees = distance_km/111.317
        queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
        return render_to_kmz(templateFolder + "/recommendPineapple.kml", {'places' : queryset})

def recommendSweetPotato(request,lat=0,lon=0,radius=0):
        origin = Point(float(lon),float(lat))
        distance_km = (float(radius)/1000)+0.01
        circle = origin.buffer((float(radius)/1000)/111.317)
        distance_degrees = distance_km/111.317
        queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
        return render_to_kmz(templateFolder + "/recommendSweetPotato.kml", {'places' : queryset})

def recommendCitrus(request,lat=0,lon=0,radius=0):
        origin = Point(float(lon),float(lat))
        distance_km = (float(radius)/1000)+0.01
        circle = origin.buffer((float(radius)/1000)/111.317)
        distance_degrees = distance_km/111.317
        queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
        return render_to_kmz(templateFolder + "/recommendCitrus.kml", {'places' : queryset})

def soilAndRainfall(request,lat=0,lon=0,radius=0):
        origin = Point(float(lon),float(lat))
        distance_km = (float(radius)/1000)+0.01
        circle = origin.buffer((float(radius)/1000)/111.317)
        distance_degrees = distance_km/111.317
        queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
        return render_to_kmz(templateFolder + "/recommendOverlay.kml", {'places' : queryset})

def cropSpecifications(request):
	lettuce={"highph":6.5,"lowph":6.0,"highRainfall":1390,"lowRainfall":1320,"highClay":40,"lowClay":8}
	breadfruit={"highph":7.4,"lowph":6.1,"highRainfall":3000,"lowRainfall":1500,"highClay":35,"lowClay":10}
	papaya={"highph":6.5,"lowph":5.5,"highRainfall":1219,"lowRainfall":800,"highClay":30,"lowClay":15}
	tomato={"highph":6.8,"lowph":6.0,"highRainfall":1825,"lowRainfall":1622,"highClay":28,"lowClay":15}
	corn={"highph":7.0,"lowph":5.5,"highRainfall":3900,"lowRainfall":2600,"highClay":55,"lowClay":20}
	pigeonpea={"highph":6.8,"lowph":6.0,"highRainfall":1000,"lowRainfall":600,"highClay":30,"lowClay":18}
	citrus={"highph":7.0,"lowph":6.0,"highRainfall":2190,"lowRainfall":1825,"highClay":30,"lowClay":15}
	sweetpepper={"highph":7.0,"lowph":5.5,"highRainfall":2600,"lowRainfall":1300,"highClay":28,"lowClay":15}
	cucumber={"highph":7.0,"lowph":5.5,"highRainfall":2600,"lowRainfall":1300,"highClay":28,"lowClay":15}
	pineapple={"highph":5.5,"lowph":4.5,"highRainfall":1200,"lowRainfall":600,"highClay":55,"lowClay":15}
	sweetpotato={"highph":6.8,"lowph":5.5,"highRainfall":2190,"lowRainfall":1825,"highClay":40,"lowClay":15}
	onion={"highph":6.8,"lowph":6.0,"highRainfall":3200,"lowRainfall":1900,"highClay":35,"lowClay":10}
	cassava={"highph":6.5,"lowph":4.5,"highRainfall":1500,"lowRainfall":1000,"highClay":35,"lowClay":10}
	cocoa={"highph":7.5,"lowph":5.0,"highRainfall":1800,"lowRainfall":1300,"highClay":40,"lowClay":30}
	cropObjects = {"recommendCocoa":cocoa,"recommendCassava":cassava,"recommendSweetPotato":sweetpotato,"recommendLettuce":lettuce,"recommendSweetPepper":sweetpepper,"recommendTomato":tomato,"recommendPineapple":pineapple,"recommendPigeonPea":pigeonpea,"recommendCorn":corn,"recommendCitrus":citrus,"recommendOnion":onion,"recommendCucumber":cucumber,"recommendPapaya":papaya,"recommendBreadfruit":breadfruit}
	return JsonResponse(cropObjects)

def recommendAll(request,lat=0,lon=0,radius=0):
	origin = Point(float(lon),float(lat))
	distance_km = (float(radius)/1000)+0.01
	circle = origin.buffer((float(radius)/1000)/111.317)
	distance_degrees = distance_km/111.317
	queryset = soilsAndRainfall.objects.select_related('soilFeatures').filter(geomData__dwithin=(origin, distance_degrees)).intersection(circle).kml()
	return render_to_kmz(templateFolder + "/recommendAll.kml", {'places' : queryset})
