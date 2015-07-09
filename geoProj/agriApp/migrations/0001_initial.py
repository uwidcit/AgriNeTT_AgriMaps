# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Roads',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('objectID', models.IntegerField()),
                ('fnode', models.IntegerField()),
                ('tnode', models.IntegerField()),
                ('length', models.IntegerField()),
                ('streetgis', models.CharField(max_length=150)),
                ('source', models.CharField(max_length=100)),
                ('geomData', django.contrib.gis.db.models.fields.MultiLineStringField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
