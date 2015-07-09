from django.contrib.gis.utils import LayerMapping
from models import Soils

soilsJsonFile = '/Users/kiran/Downloads/soils.json'
mapping = {
	'soilCode':'SOILCODE',
	'soilName':'SOILNAME',
	'soilLabel':'SOIL_LABEL',
	'tobacco':'TOBACCO',
	'pasture':'PASTURE',
	'food_plant':'FOOD_PLANT',
	'rice':'RICE',
	'sugarcane':'SUGARCANE',
	'timber':'TIMBER',
	'fruit_tree':'FRUIT_TREE',
	'banana':'BANANA',
	'coffee':'COFFEE',
	'coconut':'COCONUT',
	'citrus':'CITRUS',
	'cacao':'CACAO',
	'mapunit':'MAPUNIT',
	'hectares':'HECTARES',
	'acres':'ACRES',
	'perimeter':'PERIMETER_',
	'area_kilom':'AREA_KILOM',
	'comments':'COMMENTS',
	'secerosion':'SECEROSION',
	'terslope':'TERSLOPE',
	'secslope':'SECSLOPE',
	'secsoil':'SECSOIL',
	'capability':'CAPABILITY',
	'other_fea':'OTHER_FEA',
	'water':'WATER',
	'erosion':'EROSION',
	'domslope':'DOMSLOPE',
	'domsoil':'DOMSOIL',
	'soil2001_i':'SOIL2001_I',
	'soil2001':'SOIL2001_',
	'gavprimary':'GAVPRIMARY',
	'geomData':'MULTIPOLYGON'
}

lm = LayerMapping(Soils,soilsJsonFile,mapping)
lm.save(verbose=True)