# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Soils',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('soilID', models.CharField(default=None, max_length=150)),
                ('soilCode', models.CharField(max_length=25)),
                ('soilName', models.CharField(max_length=150)),
                ('soilLabel', models.CharField(max_length=150)),
                ('tobacco', models.CharField(max_length=25)),
                ('pasture', models.CharField(max_length=25)),
                ('food_plant', models.CharField(max_length=25)),
                ('rice', models.CharField(max_length=25)),
                ('sugarcane', models.CharField(max_length=25)),
                ('timber', models.CharField(max_length=25)),
                ('fruit_tree', models.CharField(max_length=25)),
                ('banana', models.CharField(max_length=25)),
                ('coffee', models.CharField(max_length=25)),
                ('coconut', models.CharField(max_length=25)),
                ('citrus', models.CharField(max_length=25)),
                ('cacao', models.CharField(max_length=25)),
                ('mapunit', models.CharField(max_length=25)),
                ('hectares', models.FloatField()),
                ('acres', models.FloatField()),
                ('perimeter', models.FloatField()),
                ('area_kilom', models.FloatField()),
                ('comments', models.CharField(max_length=150)),
                ('secerosion', models.IntegerField()),
                ('terslope', models.IntegerField()),
                ('secslope', models.IntegerField()),
                ('secsoil', models.CharField(max_length=30)),
                ('capability', models.IntegerField()),
                ('other_fea', models.CharField(max_length=25)),
                ('water', models.CharField(max_length=25)),
                ('erosion', models.IntegerField()),
                ('domslope', models.IntegerField()),
                ('domsoil', models.CharField(max_length=25)),
                ('soil2001_i', models.IntegerField()),
                ('soil2001', models.IntegerField()),
                ('gavprimary', models.IntegerField()),
                ('geomData', django.contrib.gis.db.models.fields.MultiPolygonField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
