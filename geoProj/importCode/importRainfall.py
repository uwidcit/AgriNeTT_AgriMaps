from django.contrib.gis.utils import LayerMapping
from models import Rainfall

rainfallJsonFile = '/Users/kiran/Downloads/rainfall.json'
mapping = {
	'ranges':'RANGES',
	'rain_range':'RAIN_RANGE',
	'class_r':'Class',
	'geomData':'POLYGON'
}

lm = LayerMapping(Rainfall,rainfallJsonFile,mapping)
lm.save(verbose=True)