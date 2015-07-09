from django.contrib.gis.utils import LayerMapping
from models import RainfallData

RainfallshpFile = '/home/kiran/mapFiles/Rainfall/Export_Output.shp'
mapping = {
	'gridcode' : 'GRIDCODE',
    'geomData' : 'POLYGON'
}

lm = LayerMapping(RainfallData,RainfallshpFile,mapping)
lm.save(verbose=True)
